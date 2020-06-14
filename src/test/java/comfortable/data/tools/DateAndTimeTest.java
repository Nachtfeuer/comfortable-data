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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing of class {@link DateAndTime}.
 */
public class DateAndTimeTest {
    /**
     * Test date and time.
     */
    private static final String TEST_DATE_AND_TIME = "2020-06-12T04:36:25Z";
    
    /**
     * Test epoch seconds for the given date and time.
     */
    private static final long TEST_EPOCH_SECONDS = 1591936585L;

    /**
     * Testing {@link DateAndTime#now()}. Since I don't want copy'n paste
     * the implementation I tried to find another solution on how to get
     * the epoch seconds.
     */
    @Test
    public void testNow() {
        final var currenDateAndTimeInUTC = DateAndTime.now();
        // because it's a local date and time you have to specify the offset twice
        // first time to create a date and time at given time zone and second time
        // when converting "local" date time to epoch seconds otherwise it really
        // uses "local".
        final var epochSeconds = LocalDateTime.now(ZoneOffset.UTC)
                .toEpochSecond(ZoneOffset.UTC);
        assertThat(currenDateAndTimeInUTC.getValue(), equalTo(epochSeconds));
    }

    /**
     * Have been using https://www.epochconverter.com/ to check the value.
     */
    @Test
    public void testParse() {
        assertThat(DateAndTime.parse(TEST_DATE_AND_TIME).getValue(),
                   equalTo(TEST_EPOCH_SECONDS));
    }
    
    @Test
    public void testToString() {
        assertThat(new DateAndTime(TEST_EPOCH_SECONDS).toString(),
                   equalTo(TEST_DATE_AND_TIME));
    }
}
