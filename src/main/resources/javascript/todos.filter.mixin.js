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
let filterMixin = {
    methods: {
        isSearchText: function (todo) {
            const searchText = this.search.toLowerCase();
            return todo.title.toLowerCase().includes(searchText);
        },

        isAll: function (todo, recognizeSearchText = true) {
            return !recognizeSearchText || this.isSearchText(todo);
        },

        /**
         * Checks completion state for done.
         * Also it does recognize the current search filter.
         *
         * @param {object} todo ui todo structure.
         * @param {boolean} recognizeSearchText when true (default) then recognized search text.
         * @returns {Boolean} true when todo is done.
         */
        isCompleted: function (todo, recognizeSearchText = true) {
            return todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        /**
         * Checks completion state for not done.
         * Also it does recognize the current search filter.
         *
         * @param {object} todo ui todo structure.
         * @param {boolean} recognizeSearchText when true (default) then recognized search text.
         * @returns {Boolean} true when todo is not done.
         */
        isNotCompleted: function (todo, recognizeSearchText = true) {
            return !todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        /**
         * Filter function that checks a todo priority equal to 'A' independent
         * of its completion state. Also it does recognize the current search filter.
         * 
         * @param {object} todo ui todo structure.
         * @param {boolean} recognizeSearchText when true (default) then recognized search text.
         * @returns {Boolean} when todo is of high priority.
         */
        isHighPriority: function (todo, recognizeSearchText = true) {
            return todo.priority === 'A'
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        /**
         * Filter function that checks a todo priority equal to 'A' but not completed.
         * Also it does recognize the current search filter.
         * 
         * @param {object} todo ui todo structure.
         * @param {boolean} recognizeSearchText when true (default) then recognized search text.
         * @returns {Boolean} when todo is of high priority.
         */
        isHighPriorityAndNotCompleted: function (todo, recognizeSearchText = true) {
            return todo.priority === 'A'
                    && !todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        /**
         * Check for given todo that last change was today.
         *
         * @param {type} todo concrete todo
         * @param {type} recognizeSearchText consider search filter (default: no)
         * @returns {Boolean} true when given today way changed today.
         */
        isToday: function (todo, recognizeSearchText = true) {
            let now = new Date();
            let changed = new Date(todo.changed);

            let isCondition = now.getYear() === changed.getYear() &&
                    now.getMonth() === changed.getMonth() &&
                    now.getDay() === changed.getDay();

            if (recognizeSearchText) {
                isCondition = isCondition && this.isSearchText(todo);
            }

            return isCondition;
        },

        /**
         * Check for given todo that last change was yesterday.
         *
         * @param {type} todo concrete todo
         * @param {type} recognizeSearchText consider search filter (default: no)
         * @returns {Boolean} true when given today way changed yesterday.
         */
        isYesterday: function (todo, recognizeSearchText = true) {
            let yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);

            let changed = new Date(todo.changed);
            let isCondition = yesterday.getYear() === changed.getYear() &&
                    yesterday.getMonth() === changed.getMonth() &&
                    yesterday.getDay() === changed.getDay();

            if (recognizeSearchText) {
                isCondition = isCondition && this.isSearchText(todo);
            }

            return isCondition;
        },

        hasTag: function (name, criteria = undefined) {
            return function (todo, recognizeSearchText = true) {
                return todo.tags.findIndex(entry => entry === name) >= 0
                        && (criteria === undefined || criteria(todo, recognizeSearchText));
            };
        },

        /**
         * Combined filtter for todos with given project name and optional
         * another filter (useful for all not completed todos).
         *
         * @param {string} name of the project to filter for.
         * @param {Function} another criteria criteria for filter or for counting.
         * @returns {Function} criteria that does filter todos with given project name.
         */
        hasProject: function (name, criteria = undefined) {
            return function (todo, recognizeSearchText = true) {
                return todo.projects.findIndex(entry => entry === name) >= 0
                        && (criteria === undefined || criteria(todo, recognizeSearchText));
            };
        }
    }
};
