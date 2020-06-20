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
package comfortable.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Priority based on characters; 'A' is highest priority. The unknown priority is represented by a
 * blank and represents the lowest priority.
 */
public enum Priority {
    /**
     * Highest priority.
     */
    A('A'),
    /**
     * Second priority.
     */
    B('B'),
    /**
     * Third priority.
     */
    C('C'),
    /**
     * Fourth priority.
     */
    D('D'),
    /**
     * Fifth priority.
     */
    E('E'),
    /**
     * Sixth priority.
     */
    F('F'),
    /**
     * Lowest priority.
     */
    UNKNOWN(' ');

    /**
     * Value of the concrete enum.
     */
    private final char priority;

    /**
     * Initialize enum with concrete value.
     *
     * @param initialPriority the internal value for the given priority.
     */
    Priority(final char initialPriority) {
        this.priority = initialPriority;
    }

    @Override
    public String toString() {
        return String.valueOf(this.priority);
    }

    /**
     * Get the real priority value.
     *
     * @return priority.
     */
    @JsonValue
    public char getValue() {
        return this.priority;
    }

    /**
     * Convert a priority string to a priority enum value.
     *
     * @param value string value representing the priority
     * @return enum value
     */
    @JsonCreator
    public static Priority fromString(final String value) {
        final Priority priority;

        switch (value) {
            case "A":
                priority = A;
                break;
            case "B":
                priority = B;
                break;
            case "C":
                priority = C;
                break;
            case "D":
                priority = D;
                break;
            case "E":
                priority = E;
                break;
            case "F":
                priority = F;
                break;
            default:
                if (!value.isBlank()) {
                    throw new IllegalArgumentException("Unknown enum value \"" + value + "\"");
                }
                priority = UNKNOWN;
        }
        return priority;
    }
}
