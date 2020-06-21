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
let sortingMixin = {
    methods: {
        /**
         * Comparison of two todo's.
         * The main criteria is priority.
         *
         * @param {type} todoA the one todo for comparison.
         * @param {type} todoB the other todo for comparison.
         * @returns {Number} -1 for less than, 0 for equal, +1 for greater than
         */
        defaultCriteria: function (todoA, todoB) {
            let diff = {false: 0, true: 1}[todoA.completed]
                    - {false: 0, true: 1}[todoB.completed];

            if (diff === 0) {
                if (todoA.priority < todoB.priority) {
                    return -1;
                }
                if (todoA.priority > todoB.priority) {
                    return 1;
                }


                if (diff === 0) {
                    diff = todoA.title.localeCompare(todoB.title);
                }
            }

            return diff;
        },
        
        /**
         * Comparison of two todo's.
         * The main criteria is the change date and time.
         *
         * @param {type} todoA the one todo for comparison.
         * @param {type} todoB the other todo for comparison.
         * @returns {Number} -1 for less than, 0 for equal, +1 for greater than
         */
        recentChangesCriteria: function(todoA, todoB) {
            let changedA = new Date(todoA.changed);
            let changedB = new Date(todoB.changed);

            if (changedA > changedB) {
                return -1;
            } else if (changedA < changedB) {
                return 1;
            }
            
            return this.defaultCriteria(todoA, todoB);
        }
    }
};
