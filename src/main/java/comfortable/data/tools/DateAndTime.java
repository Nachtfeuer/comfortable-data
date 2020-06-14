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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplifying date and time handing in utc.
 * <p>
 * Internal value is a long (epoch seconds since 1970-01-01T00:00:00Z);
 * this makes it easier to keep the value small and transportable.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = DateAndTimeSerializer.class)
@JsonDeserialize(using = DateAndTimeDeserializer.class)
public class DateAndTime implements Serializable {
    /**
     * version of class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Date and time in UTC as epoch seconds since 1970-01-01T00:00:00Z.
     */
    private long value;

    /**
     * Get current date and time in UTC.
     * Milliseconds are ignored.
     *
     * @return instance of class {@link DateAndTime}.
     */
    public static DateAndTime now() {
        return new DateAndTime(
                OffsetDateTime.now(ZoneOffset.UTC).withNano(0).toEpochSecond());
    }
    
    /**
     * Convert ISO-8601 date and time format into instance of {@link DateAndTime}.
     *
     * @param strDateAndTime Date and time in ISO-8601 format.
     * @return instance of class {@link DateAndTime}.
     */
    public static DateAndTime parse(final String strDateAndTime) {
        return new DateAndTime(
            OffsetDateTime.parse(strDateAndTime).withNano(0).toEpochSecond());
    }

    /**
     * Convert epoch seconds into OffsetDateTime (UTC).
     *
     * @return instance of {@link OffsetDateTime}.
     */
    public OffsetDateTime toOffsetDateTime() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(this.value), ZoneId.of("UTC"));
    }

    /**
     * Provide epoch seconds as date and time in ISO-8601 format.
     * @return string with date and time in ISO-8601 format.
     */
    @Override
    public String toString() {
        return toOffsetDateTime().toString();
    }
}
