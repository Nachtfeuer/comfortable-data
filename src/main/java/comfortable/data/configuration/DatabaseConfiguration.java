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
package comfortable.data.configuration;

import java.nio.file.Paths;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the database.
 */
@Configuration
public class DatabaseConfiguration {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    /**
     * Application configuration.
     */
    @Autowired
    private transient ApplicationConfiguration configuration;

    /**
     * If given the url can be overwritten via application.properties.
     */
    @Value("${spring.datasource.url}")
    private transient String strDatabaseUrl;
    
    /**
     * All except the url is specifified in the application.properties.
     * @return data source.
     */
    @ConfigurationProperties(prefix="spring.datasource")
    @Bean
    public DataSource getDataSource() {
        final var builder = DataSourceBuilder.create();
        final String strUrl;

        if (this.strDatabaseUrl == null) {
            final var strDatabasePath = Paths.get(
                configuration.getRootPath().toString(), "comfortable.data");        
            strUrl = "jdbc:h2:file:" + strDatabasePath.toString() + ";DB_CLOSE_ON_EXIT=FALSE";
        } else {
            strUrl = this.strDatabaseUrl;
        }
        // will be overwritten by application.properties when defined there        
        LOGGER.info("Default Database url: {}", strUrl);
        builder.url(strUrl);
        return builder.build();
    }    
}
