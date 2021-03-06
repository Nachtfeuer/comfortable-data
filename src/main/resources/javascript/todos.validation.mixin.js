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
let validationMixin = {
    data: {
        error_messages: {}        
    },

    watch: {
        'todo.title': function(value) {
            this.validateTitle(value);
        },
        'todo.workingTime': function(value) {
            this.validateWorkingTime(value);
        },
        'todo.estimatedWorkingTime': function(value) {
            this.validateEstimatedWorkingTime(value);
        }
    },

    methods: {
        validateTitle: function(value) {
            if (value.length === 0) {
                this.error_messages.title = '**Invalid title**: must not be empty!';
            } else {
                delete this.error_messages.title;
            }
        },

        /**
         * Validate the format of the human readable working time.
         *
         * @param {string} value workinng Time human readable
         */
        validateWorkingTime: function(value) {
            const duration = this.humanReadableWorkingTimeAsSeconds(value);
            if (duration === undefined) {
                const message = '**Invalid working time:**\n\n'
                    + " * Expected format is like _2d3h4m5s_.\n"
                    + " * You can define less but you have to keep the order.\n"
                    + " * The numbers have to be in valid range (example: a minute is between 1 and 59).\n"
                    + " * Leaving the field empty means no working time";
                this.error_messages.workingTime = message;
            } else {
                delete this.error_messages.workingTime;
            }
        },

        /**
         * Validate the format of the human readable estimated working time.
         *
         * @param {string} value workinng Time human readable
         */
        validateEstimatedWorkingTime: function(value) {
            const duration = this.humanReadableWorkingTimeAsSeconds(value);
            if (duration === undefined) {
               const message = '**Invalid estimation:**\n\n'
                    + " * Expected format is like _2d3h4m5s_.\n"
                    + " * You can define less but you have to keep the order.\n"
                    + " * The numbers have to be in valid range (example: a minute is between 1 and 59).\n"
                    + " * Leaving the field empty means no estimation";
                this.error_messages.estimatedWorkingTime = message;
            } else {
                delete this.error_messages.estimatedWorkingTime;
            }
        }        
    }
};
