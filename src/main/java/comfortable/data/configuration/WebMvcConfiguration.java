/*
 * The MIT License
 *
 * Copyright 2019 Thomas Lehmann.
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
package comfortable.data.configuration;

import java.util.List;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Configuration of the Jackson YAML converter to the spring framework.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    /**
     * Time in seconds for how long to keep static files in cache.
     */
    @Value("${resources.cache.period:3600}")
    private transient Integer resourcesCachePeriod;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("classpath:/javascript/")
          .addResourceLocations("classpath:/stylesheet/")
          .addResourceLocations("classpath:/icons/")
          .setCachePeriod(this.resourcesCachePeriod)
          .resourceChain(true)
          .addResolver(new EncodedResourceResolver())
          .addResolver(new PathResourceResolver());

        registry
          .addResourceHandler("/resources/libraries/**")
          .addResourceLocations("classpath:/libraries/javascript/")
          .addResourceLocations("classpath:/libraries/stylesheet/")
          .setCachePeriod(this.resourcesCachePeriod)
          .resourceChain(true)
          .addResolver(new EncodedResourceResolver())
          .addResolver(new PathResourceResolver());
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJackson2HttpMessageConverter());
        converters.add(new MsgPackJackson2HttpMessageConverter());
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SpecificationArgumentResolver());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }    
}
