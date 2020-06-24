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
let conversionMixin = {
    methods: {
        /**
         * Convert priority character as number.
         *
         * @param {char} priority priority as character
         * @returns {number} priority as number
         */
        priorityAsNumber: function(priority) {
            return {A: 0, B: 1, C:2, D:3, E:4, F:5, ' ':6}[priority];
        },

        /**
         * Convert complexity string as number.
         *
         * @param {string} complexity as string
         * @returns {number} complexity as number
         */
        complexityAsNumber: function(complexity) {
            return {XS: 0, S: 1, M:2, L:3, XL:4}[complexity];
        },

        /**
         * Convert duration in seconds into a human readable string.
         *
         * @param {number} duration in seconds
         * @returns {String} duration as hum readable string
         */
        workingTimeAsHumanReadableString: function (duration) {
            const days = (duration >= (60 * 60 * 24)) ? Math.trunc(duration / (60 * 60 * 24)) : 0;
            duration = duration - 60 * 60 * 24 * days;
            const hours = (duration >= (60 * 60)) ? Math.trunc(duration / (60 * 60)) : 0;
            duration = duration - 60 * 60 * hours;
            const minutes = (duration >= 60) ? Math.trunc(duration / 60) : 0;
            duration = duration - 60 * minutes;
            const seconds = duration;

            const text = ""
                    + ((days > 0) ? " " + days + "d" : "")
                    + ((hours > 0) ? " " + hours + "h" : "")
                    + ((minutes > 0) ? " " + minutes + "m" : "")
                    + ((seconds > 0) ? " " + seconds + "s" : "");
            return text.trim();
        },
        
        /**
         * Convert human readable String into second.
         *
         * @param {string} durationAsString as hum readable string.
         * @return {number} duration in seconds.
         */
        humanReadableWorkingTimeAsSeconds: function(durationAsString) {
            const details = durationAsString.match(/(\d+d)*(\d+h)*(\d+m)*(\d+s)*/);
            const factors = [60*60*24, 60*60, 60, 1];
            const limits  = [365, 23, 59, 59];

            var consumedLength = 0;
            var durationInSeconds = 0;

            for (var ix = 1; ix <= 4; ++ix) {
                if (details[ix] !== undefined) {
                    strValue = details[ix].substring(0, details[ix].length-1);
                    intValue = parseInt(strValue);
                    if (intValue > 0 && intValue <= limits[ix-1]) {
                        durationInSeconds += intValue * factors[ix-1];
                        consumedLength += details[ix].length;
                    }
                }
            }        

            if (consumedLength === durationAsString.length) {
                return durationInSeconds;
            }

            return undefined;
        },

        /**
         * Converts frontend todo structure into backend todo structure.
         *
         * @param {object} todo the ui todo structure.
         * @returns backend todo structure.
         */
        convert2Backend: function (todo) {
            let backendTodo = {
                id: todo.id,
                created: todo.created,
                changed: todo.changed,
                title: todo.title,
                description: todo.description,
                priority: todo.priority,
                complexity: todo.complexity,
                completed: todo.completed,
                tags: [],
                projects: [],
                workingTime: todo.workingTime,
                estimatedWorkingTime: this.humanReadableWorkingTimeAsSeconds(
                        todo.estimatedWorkingTime)
            };

            todo.tags.forEach(entry => backendTodo.tags.push({name: entry}));
            todo.projects.forEach(entry => backendTodo.projects.push({name: entry}));
            return backendTodo;
        },

        /**
         * Converts backend todo structure into frontend todo structure.
         *
         * @param {object} todo usually a todo as response from a REST call.
         * @returns frontend todo structure
         */
        convert2Frontend: function (todo) {
            let frontendTodo = {
                id: todo.id,
                created: todo.created,
                changed: todo.changed,
                title: todo.title,
                description: todo.description,
                priority: todo.priority,
                complexity: todo.complexity,
                completed: todo.completed,
                tags: [],
                projects: [],
                workingTime: todo.workingTime,
                estimatedWorkingTime: this.workingTimeAsHumanReadableString(
                        todo.estimatedWorkingTime)
            };

            todo.tags.forEach(entry => frontendTodo.tags.push(entry.name));
            todo.projects.forEach(entry => frontendTodo.projects.push(entry.name));
            return frontendTodo;
        }
    }
};
