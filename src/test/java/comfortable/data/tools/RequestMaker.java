/*
 * The MIT License
 *
 * Copyright 2020 Thomas Lehmann.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package comfortable.data.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import comfortable.data.model.CustomMediaType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

/**
 * Simplifying the requests in the tests.
 * @param <E> The class the requests should be made for.
 */
public class RequestMaker<E> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMaker.class);

    /**
     * The concrete class required for conversion.
     */
    private final Class<E> type;

    /**
     * mocked web layer.
     */
    private final MockMvc mvc;

    /**
     * Initializes the request make with the mvc.
     *
     * @param initType the concrete class required for conversion.
     * @param initMvc the web layer.
     */
    public RequestMaker(final Class<E> initType, final MockMvc initMvc) {
        this.type = initType;
        this.mvc = initMvc;
    }

    /**
     * Posting data via defined path and in defined format.
     *
     * @param request the relative path that represents the concrete request.
     * @param contentType the communication format (JSON, XML or YAML)
     * @param value the data to save
     * @return saved data
     * @throws Exception when request has failed.
     */
    public E postData(final String request, final MediaType contentType,
                      final E value) throws Exception {
        final var documentName = "post" + request;
        final var converterTo = getValue2StringConverter(contentType);
        final var converterFrom = getString2ValueConverter(contentType);

        final var responseContent = this.mvc.perform(post(request)
                .accept(contentType)
                .contentType(contentType)
                .content(converterTo.convert(value)))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        return converterFrom.convert(responseContent);
    }

    /**
     * Deleting a record.
     *
     * @param request delete request
     * @return String of the response.
     * @throws Exception when request has failed.
     */
    public String deleteData(final String request) throws Exception {
        final var documentName = "delete" + request;

        return this.mvc.perform(delete(request))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Get data via defined path and in defined format.
     *
     * @param request the relative path that represents the concrete request.
     * @param contentType the communication format (JSON, XML or YAML)
     * @return saved data
     * @throws Exception when request has failed.
     */
    public List<E> getData(final String request, final MediaType contentType) throws Exception {
        final var documentName = "get" + request.replaceAll("\\?.*", "");

        final var content = this.mvc.perform(get(request)
                .accept(contentType))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        return convertString2ListOfValue(content, contentType);
    }
    
    /**
     * Get one data (not a list).
     *
     * @param request the relative REST path representing the concrete request
     * @param expectedResponseType XML or JSON
     * @return concrete data (record)
     * @throws Exception (should never happen)
     */
    public List<E> getListOfData(final String request,
                                 final MediaType expectedResponseType) throws Exception {
        final String content = this.mvc.perform(get(request)
                .accept(expectedResponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return convertString2ListOfValue(content, expectedResponseType);
    }

    /**
     * Converts a string to a list of data.
     *
     * @param content string content
     * @param contentType the format of the string content (XML, JSON or YAML).
     * @return List of data or null if not support
     * @throws JsonProcessingException when conversion has failed.
     */
    private List<E> convertString2ListOfValue(
            final String content, final MediaType contentType) throws JsonProcessingException {
        final List<E> data;

        if (contentType == CustomMediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            data = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, this.type));
        } else if (contentType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            data = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, this.type));
        } else if (contentType == MediaType.APPLICATION_XML) {
            final var xmlMapper = new XmlMapper();
            data = xmlMapper.readValue(content,
                    xmlMapper.getTypeFactory().constructCollectionType(List.class, this.type));
        } else {
            data = null;
        }
        
        return data;
    }
    
    /**
     * Get the right value to string converter for given media type.
     * Supported are JSON, XML and YAML.
     * 
     * @param contentType media type.
     * @return interface to converter function or null if not supported.
     */
    private IConvertValue2String<E> getValue2StringConverter(final MediaType contentType) {
       final IConvertValue2String<E> converter;

        if (contentType == CustomMediaType.APPLICATION_JSON) {
            final var jsonMapper = new ObjectMapper();
            converter = theValue -> {
                String strValue;
                try {
                    strValue = jsonMapper.writeValueAsString(theValue);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    strValue = null;
                }
                return strValue;
            };
        } else if (contentType == CustomMediaType.APPLICATION_YAML) {
            final var yamlMapper = new ObjectMapper(new YAMLFactory());
            converter = theValue -> {
                String strValue;
                try {
                    strValue = yamlMapper.writeValueAsString(theValue);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    strValue = null;
                }
                return strValue;
            };
        } else if (contentType == CustomMediaType.APPLICATION_XML) {
            final var xmlMapper = new XmlMapper();
            converter = theValue -> {
                String strValue;
                try {
                    strValue = xmlMapper.writeValueAsString(theValue);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    strValue = null;
                }
                return strValue;
            };
        } else {
            converter = null;
        }

        return converter;
    }

    /**
     * Get the right string to value converter for given media type.
     * Supported are JSON, XML and YAML.
     * 
     * @param contentType media type.
     * @return interface to converter function or null if not supported.
     */
    private IConvertString2Value<E> getString2ValueConverter(final MediaType contentType) {
       final IConvertString2Value<E> converter;

       if (contentType == CustomMediaType.APPLICATION_JSON) {
            final var jsonMapper = new ObjectMapper();
            converter = theValue -> {
                E value;
                try {
                    value = jsonMapper.readValue(theValue, this.type);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    value =  null;
                }
                return value;
            };
        } else if (contentType == CustomMediaType.APPLICATION_YAML) {
            final var yamlMapper = new ObjectMapper(new YAMLFactory());
            converter = theValue -> {
                E value;
                try {
                    value = yamlMapper.readValue(theValue, this.type);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    value = null;
                }
                return value;
            };
        } else if (contentType == CustomMediaType.APPLICATION_XML) {
            final var xmlMapper = new XmlMapper();
            converter = theValue -> {
                E value;
                try {
                    value = xmlMapper.readValue(theValue, this.type);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                    value = null;
                }
                return value;
            };
        } else {
            converter = null;
        }
       
       return converter;
    }
}
