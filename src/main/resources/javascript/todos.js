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
    data: {
        todos: [],
        search: '',
        currentFilter: undefined,
        working: {
            id: undefined, // Id of the todo
            started: undefined     // Start Date and time (new Date())
        },
        workingTimeHumanReadable: '',
        todo: {}
    },

    created: function () {
        this.currentFilter = this.isNotCompleted;

        if (localStorage.search) {
            this.search = localStorage.search;
        }

        if (localStorage.working_id && localStorage.working_start) {
            this.working = {
                id: parseInt(localStorage.working_id),
                start: new Date(localStorage.working_start)
            };
        }

        axios.get('/todos').then(response => {
            if (response.status === 200) {
                this.todos = [];
                response.data.forEach(
                        entry => this.todos.push(this.convert2Frontend(entry)));
            }
        });

        setInterval(() => {
            this.workingTimeHumanReadable = this.calculateHumanReadableWorkingTime(
                    this.getWorkingTime())
        }, 1000);
    },

    computed: {
        filteredTodos: function () {
            return this.todos.filter(this.currentFilter).sort(this.defaultCriteria);
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
                completed: todo.completed,
                tags: [],
                projects: [],
                workingTime: todo.workingTime
            };

            todo.tags.forEach(entry => frontendTodo.tags.push(entry.name));
            todo.projects.forEach(entry => frontendTodo.projects.push(entry.name));
            return frontendTodo;
        },

        createTodo: function () {
            const theTodo = this.convert2Backend(this.todo);
            console.log("trying to create new todo \"" + theTodo.title + "\"");

            axios.post('/todos', theTodo).then((response) => {
                if (response.status === 200) {
                    const theTodoSaved = this.convert2Frontend(response.data);
                    console.log("successfully created new todo \"" + theTodoSaved.title + "\"");
                    this.todos.push(theTodoSaved);
                }
            });
        },

        editTodo: function (todo) {
            this.todo = this.copyFrontend(todo);
            $('#edit-todo').modal();
        },

        checkDelete: function (todo) {
            this.todo = this.copyFrontend(todo);
            $('#do-you-really-want-to-delete').modal();
        },

        deleteTodo: function (todo) {
            console.log("trying to delete \"" + todo.title + "\"");
            axios.delete('/todos/' + todo.id).then((response) => {
                if (response.status === 200) {
                    console.log("successfully deleted " + todo.title + "\"");
                    const pos = this.todos.findIndex(entry => entry.id === todo.id);
                    this.$delete(this.todos, pos);
                }
            });
        },

        updateTodo: function () {
            const theTodo = this.convert2Backend(this.todo);
            console.log("trying to update todo \"" + theTodo.title + "\"");

            axios.post('/todos', theTodo).then((response) => {
                if (response.status === 200) {
                    const theTodoSaved = this.convert2Frontend(response.data);
                    console.log("successfully updated todo \"" + theTodoSaved.title + "\"");
                    const pos = this.todos.findIndex(entry => entry.id === theTodoSaved.id);
                    Vue.set(this.todos, pos, theTodoSaved);
                }
            });

        },

        /**
         * Toggle completion state.
         *
         * @param {object} todo for which the completion state should be toggled.
         */
        toggleCompletion: function (todo) {
            this.todo = this.copyFrontend(todo);
            this.todo.completed = !this.todo.completed;
            this.updateTodo();
        },

        changeToFilter: function (newFilter) {
            this.search = '';
            this.currentFilter = newFilter;
        },

        isSearchText: function (todo) {
            const searchText = this.search.toLowerCase();
            return todo.title.toLowerCase().includes(searchText);
        },

        isAll: function (todo, recognizeSearchText = true) {
            return !recognizeSearchText || this.isSearchText(todo);
        },

        isCompleted: function (todo, recognizeSearchText = true) {
            return todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        isNotCompleted: function (todo, recognizeSearchText = true) {
            return !todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        isHighPriorityAndNotCompleted: function (todo, recognizeSearchText = true) {
            return todo.priority === 'A'
                    && !todo.completed
                    && (!recognizeSearchText || this.isSearchText(todo));
        },

        isHighPriority: function (todo, recognizeSearchText = true) {
            return todo.priority === 'A'
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
