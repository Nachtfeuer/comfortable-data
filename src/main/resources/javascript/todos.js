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

new Vue({
    el: '#content',
    mixins: [filterMixin, sortingMixin, crudMixin, validationMixin],
    data: {
        todos: [],
        search: '',
        sorting: 'default',
        currentFilter: undefined,
        currentCriteria: undefined,
        working: {
            id: undefined, // Id of the todo
            started: undefined     // Start Date and time (new Date())
        },
        workingTimeHumanReadable: '',
        todo: {}
    },

    created: function () {
        this.currentFilter = this.isNotCompleted;
        this.currentCriteria = this.defaultCriteria;

        if (localStorage.search) {
            this.search = localStorage.search;
        }

        if (localStorage.sorting) {
            this.sorting = localStorage.sorting;
            if (this.sorting === 'recent-changes') {
                this.currentCriteria = this.recentChangesCriteria;
            }
        }

        if (localStorage.working_id && localStorage.working_start) {
            this.working = {
                id: parseInt(localStorage.working_id),
                start: new Date(localStorage.working_start)
            };
        }

        this.readTodos();

        setInterval(() => {
            this.workingTimeHumanReadable = this.calculateHumanReadableWorkingTime(
                    this.getWorkingTime())
        }, 1000);
    },

    computed: {
        filteredTodos: function () {
            return this.todos.filter(this.currentFilter).sort(this.currentCriteria);
        },

        /**
         * Compute list of assigned tags and its occurences.
         *
         * @returns {Array} of tag names and its occurences.
         */
        getTags: function () {
            let statistic = {};

            this.todos.forEach(todo => {
                todo.tags.forEach(tag => {
                    if (!(tag in statistic)) {
                        statistic[tag] = 0;
                    }
                    statistic[tag] += 1;
                });
            });

            let statisticAsArray = [];
            for (let name in statistic) {
                statisticAsArray.push({name: name, count: statistic[name]});
            }

            return statisticAsArray;
        },

        /**
         * Compute list of assigned tags and its occurences.
         *
         * @returns {Array} of tag names and its occurences.
         */
        getProjects: function () {
            let statistic = {};

            this.todos.forEach(todo => {
                todo.projects.forEach(project => {
                    if (!(project in statistic)) {
                        statistic[project] = 0;
                    }
                    statistic[project] += 1;
                });
            });

            let statisticAsArray = [];
            for (let name in statistic) {
                statisticAsArray.push({name: name, count: statistic[name]});
            }

            return statisticAsArray;
        }
    },

    watch: {
        search(newSearch) {
            localStorage.search = newSearch;
        },

        working(newWorking) {
            localStorage.working_id = newWorking.id;
            localStorage.working_start
                    = newWorking.start === undefined ? '' : newWorking.start.toISOString();
        },

        sorting(newSorting) {
            localStorage.sorting = newSorting;
            switch(newSorting) {
                case 'default':
                    this.currentCriteria = this.defaultCriteria;
                    break;
                case 'recent-changes':
                    this.currentCriteria = this.recentChangesCriteria;
                    break;
            }
        }
    },

    methods: {
        startWorking: function (todo) {
            this.working = {
                id: todo.id,
                start: new Date()
            };
        },

        stopWorking: function () {
            let pos = this.todos.findIndex(todo => todo.id === this.working.id);
            this.todo = this.copyFrontend(this.todos[pos]);
            this.todo.workingTime += this.getWorkingTime();
            this.working = {id: undefined, start: undefined};
            this.updateTodo();
        },

        /**
         * Get current working time in seconds.
         *
         * @returns {Number} current working time in seconds
         */
        getWorkingTime: function () {
            if (this.working.start) {
                return Math.trunc((new Date().getTime() - this.working.start.getTime()) / 1000);
            }

            return 0;
        },

        calculateHumanReadableWorkingTime: function (duration) {
            const days = (duration >= (60 * 60 * 24)) ? Math.trunc(duration / (60 * 60 * 24)) : 0;
            duration = duration - 60 * 60 * 24 * days;
            const hours = (duration >= (60 * 60)) ? Math.trunc(duration / (60 * 60)) : 0;
            duration = duration - 60 * 60 * hours;
            const minutes = (duration >= 60) ? Math.trunc(duration / 60) : 0;
            duration = duration - 60 * minutes;
            const seconds = duration;

            return ""
                    + ((days > 0) ? " " + days + "d" : "")
                    + ((hours > 0) ? " " + hours + "h" : "")
                    + ((minutes > 0) ? " " + minutes + "m" : "")
                    + ((seconds > 0) ? " " + seconds + "s" : "");
        },

        /**
         * Adjusts todo object which will be bound to the dialog widgets
         * just short before the dialog get opened.
         */
        openCreateTodoDialog: function () {
            this.todo = {
                id: undefined,
                created: undefined,
                changed: undefined,
                title: '',
                description: '',
                priority: ' ',
                complexity: 'M',
                completed: false,
                tags: [],
                projects: []
            };

            $('#create-todo').modal();
        },

        /**
         * Copying frontend todo structure.
         *
         * @param {type} todo frontend todo data.
         * @returns copied frontend todo structure.
         */
        copyFrontend: function (todo) {
            let copiedTodo = {
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
                workingTime: todo.workingTime
            };

            todo.tags.forEach(entry => copiedTodo.tags.push(entry));
            todo.projects.forEach(entry => copiedTodo.projects.push(entry));
            return copiedTodo;
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
                workingTime: todo.workingTime
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
                workingTime: todo.workingTime
            };

            todo.tags.forEach(entry => frontendTodo.tags.push(entry.name));
            todo.projects.forEach(entry => frontendTodo.projects.push(entry.name));
            return frontendTodo;
        },

        editTodo: function (todo) {
            this.todo = this.copyFrontend(todo);
            $('#edit-todo').modal();
        },

        checkDelete: function (todo) {
            this.todo = this.copyFrontend(todo);
            $('#do-you-really-want-to-delete').modal();
        },

        changeToFilter: function (newFilter) {
            this.search = '';
            this.currentFilter = newFilter;
        },

        /**
         * Counting todos by criteria.
         *
         * @param {callback} criteria function that accepts a todo and returns
         *                   true when the todo should be counted.
         * @returns {number} the counted todos matching the criteria. 
         */
        count: function (criteria) {
            let count = 0;
            for (let ix = 0; ix < this.todos.length; ++ix) {
                if (criteria(this.todos[ix], false)) {
                    count += 1;
                }
            }
            return count;
        },
        
        /**
         * Convert t-shirt size for complexity in long text (long form).
         *
         * @param {type} complexity (t-shirt size)
         * @returns coomplexity as long form
         */
        visualizeComplexity: function(complexity) {
            return {
                XS: 'Very Small',
                S: 'Small',
                M: 'Medium',
                L: 'Large',
                XL: 'Very Large'
            }[complexity];
        }
    }
});

// register hook for dialog 'create-todo' opening for setting focus on title
$(document).ready(function () {
    $('#create-todo').on('shown.bs.modal', function () {
        $('#create-todo-title').focus();
    });

    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    });
});
