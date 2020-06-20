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
 * Defining the complexity of a todo.
 * Complexity is about how many of following has to be done:
 * <ul>
 * <li>How many classes are involved (Java)?</li>
 * <li>How many component are involved (controller, model, ...)?</li>
 * <li>How many technologies need to be changed (Java, Javascript, HTML, CSS, ...)?</li>
 * <li>How many changes in the build?</li>
 * <li>How difficult is it to test things (mocking, satisfy the code coverage limits, ...)?</li>
 * <li>How much refactoring is required?</li>
 * <li>Many changes might raise many errors from the static code analysis</li>
 * </ul>
 * It does say nothing about time; A really simple task done once can take
 * 1 minute but if you have to do it 1000 times it might take 1000 minutes.
 */
public enum Complexity {
    /**
     * Lowest complexity.
     */
    EXTRA_SMALL("XS"),

    /**
     * Small complexity.
     */
    SMALL("S"),

    /**
     * Average complexity.
     */
    MEDIUM("M"),
    
    /**
     * Larger complexity.
     */
    LARGE("L"),
    
    /**
     * Very large complexity.
     * Here you might wanna split up the tasks into smaller ones.
     */
    EXTRA_LARGE("XL");

    /**
     * Value of the concrete enum.
     */
    private final String complexity;
    
    /**
     * Initial an enum constant with its value.
     *
     * @param initialComplexity the concrete value of the complexity.
     */
    Complexity(final String initialComplexity) {
        this.complexity = initialComplexity;
    } 

    @Override
    public String toString() {
        return this.complexity;
    }

    /**
     * Get the real complexity value.
     *
     * @return complexity.
     */
    @JsonValue
    public String getValue() {
        return this.complexity;
    }

    /**
     * Convert a complexity string to a complexity enum value.
     *
     * @param value string value representing the complexity
     * @return enum value
     */
    @JsonCreator
    public static Complexity fromString(final String value) {
        final Complexity complexity;
        
        if (EXTRA_SMALL.getValue().equals(value)) {
            complexity = EXTRA_SMALL;
        } else if (SMALL.getValue().equals(value)) {
            complexity = SMALL;
        } else if (MEDIUM.getValue().equals(value)) {
            complexity = MEDIUM;
        } else if (LARGE.getValue().equals(value)) {
            complexity = LARGE;
        } else if (EXTRA_LARGE.getValue().equals(value)) {
            complexity = EXTRA_LARGE;
        } else {
            throw new IllegalArgumentException("Unknown enum value \"" + value + "\"");
        }

        return complexity;
    }
}
