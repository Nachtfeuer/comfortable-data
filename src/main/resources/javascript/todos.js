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
// register hook for dialog 'create-todo' opening for setting focus on title
$(document).ready(function () {
    $('#create-todo-dialog').on('shown.bs.modal', function () {
        $('#create-todo-title').focus();
    });

    $("#create-todo-dialog").draggable({
        handle: ".modal-header"
    });

    $("#edit-todo-dialog").draggable({
        handle: ".modal-header"
    });
});

Vue.component('priority', { props: ['value'], template: '#priority' });
Vue.component('complexity', { props: ['value'], template: '#complexity' });

new Vue({
    el: '#content',
    mixins: [filterMixin, sortingMixin, crudMixin,
             validationMixin, conversionMixin, sidebarMixin],
    data: {
        version: '',
        todos: [],
        search: '',
        sorting: 'default',
        currentFilter: undefined,
        currentCriteria: undefined,
        previewDescription: false,
        working: {
            id: undefined, // Id of the todo
            started: undefined     // Start Date and time (new Date())
        },
        workingTime: 0,
        todo: {},
        task: {},
        taskIndex: -1,
        dialog: {context: ''}
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
            } else if (this.sorting === 'complexity') {
                this.currentCriteria = this.complexityCriteria;
            }
        }

        if (localStorage.working_id && localStorage.working_start) {
            this.working = {
                id: parseInt(localStorage.working_id),
                start: new Date(localStorage.working_start)
            };
        }

        this.readTodos();
        this.readStatus();

        setInterval(() => {
            this.workingTime = this.getWorkingTime();
        }, 1000);
    },

    computed: {
        filteredTodos: function () {
            return this.todos.filter(this.currentFilter).sort(this.currentCriteria);
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
                case 'complexity':
                    this.currentCriteria = this.complexityCriteria;
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
            
            const value = this.humanReadableWorkingTimeAsSeconds(this.todo.workingTime);
            const newValue = value + this.getWorkingTime();
            this.todo.workingTime = this.workingTimeAsHumanReadableString(
                    newValue);

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
                projects: [],
                workingTime: '',
                estimatedWorkingTime: '',
                tasks: []
            };

            this.dialog.context = 'create';
            $('#create-todo-dialog').modal();
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
                workingTime: todo.workingTime,
                estimatedWorkingTime: todo.estimatedWorkingTime,
                tasks: []
            };

            todo.tags.forEach(entry => copiedTodo.tags.push(entry));
            todo.projects.forEach(entry => copiedTodo.projects.push(entry));
            todo.tasks.forEach(entry => copiedTodo.tasks.push(
                    {description: entry.description, completed: entry.completed}));
            return copiedTodo;
        },

        editTodo: function (todo) {
            this.previewDescription = false;
            this.todo = this.copyFrontend(todo);
            this.dialog.context = 'edit';
            $('#edit-todo-dialog').modal();
        },
        
        openCreateTaskDialog: function(todo) {
            this.todo = this.copyFrontend(todo);
            this.task ={description: '', completed: false};
            this.dialog.context = 'create';
            $('#task-dialog').modal();
        },

        editTaskDialog: function(todo, index) {
            this.todo = this.copyFrontend(todo);
            this.taskIndex = index;
            this.task = {
                description: this.todo.tasks[index].description,
                completed: this.todo.tasks[index].completed};
            this.dialog.context = 'edit';
            $('#task-dialog').modal();
        },
        
        checkDeleteTask: function(todo, task, index) {
            this.todo = this.copyFrontend(todo);
            this.task = {description: task.description, completed: task.completed};
            this.taskIndex = index;
            $('#do-you-really-want-to-delete-task-dialog').modal();
        },
        
        toggleCompletionTask: function(todo, task, index) {
            console.log("Toggle completion for task " + task);
            this.todo = this.copyFrontend(todo);
            this.todo.tasks[index].completed = !this.todo.tasks[index].completed;
            this.updateTodo();
        },

        editTask: function(todo, task, index) {
            
        },

        checkDelete: function (todo) {
            this.todo = this.copyFrontend(todo);
            $('#do-you-really-want-to-delete-dialog').modal();
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
        },
        
        renderMarkdown: function(description) {
            const converter = new showdown.Converter();
            converter.setOption('headerStartLevel', 3);
            return converter.makeHtml(description);
        },
        
        togglePreviewDescription: function() {
            this.previewDescription = !this.previewDescription;
        },
        
        /**
         * Collecting details for a concrete quick filter.
         *
         * @param {type} name name of the quick filter
         * @param {type} criteria filter for todos
         * @returns {string} HTML for tooltip dispaying details
         */
        getDetails: function(name, criteria) {
            const filteredTodos = this.todos.filter(criteria);
            let countNotCompleted = 0;
            let sumNotCompletedWorkingTime = 0;
            let sumOverallWorkingTime = 0;
            let sumNotCompletedEstimatedWorkingTime = 0;
            let sumOverallEstimatedWorkingTime = 0;

            filteredTodos.forEach(todo => {
                const workingTime = this.humanReadableWorkingTimeAsSeconds(todo.workingTime);
                const estimatedWorkingTime = this.humanReadableWorkingTimeAsSeconds(todo.estimatedWorkingTime);
                if (!todo.completed) {
                    countNotCompleted += 1;
                    sumNotCompletedWorkingTime += workingTime;
                    sumNotCompletedEstimatedWorkingTime += estimatedWorkingTime;
                }
                sumOverallWorkingTime += workingTime;
                sumOverallEstimatedWorkingTime += estimatedWorkingTime;
            });
            
            const details = '### ' + name + '\n'
                + ' - *Not completed todos*: ' + countNotCompleted + '\n'
                + ' - *Overall todos*: ' + filteredTodos.length + '\n'
                + ' - *Sum Not Completed Working Time*: ' + this.workingTimeAsHumanReadableString(sumNotCompletedWorkingTime) + '\n'
                + ' - *Sum Overall Working Time*: ' + this.workingTimeAsHumanReadableString(sumOverallWorkingTime) + '\n'
                + ' - *Sum Not Completed Working Time (estimated)*: ' + this.workingTimeAsHumanReadableString(sumNotCompletedEstimatedWorkingTime) + '\n'
                + ' - *Sum Overall Working Time (estimated)*: ' + this.workingTimeAsHumanReadableString(sumOverallEstimatedWorkingTime);

            return this.renderMarkdown(details);
        },
        
        percentageCompletion: function(todo) {
            if (todo.tasks.length === 0) {
                return 0.0;
            } else {
                var counter = 0;
                todo.tasks.forEach(task => {
                    if (task.completed) {
                        counter += 1;
                    }
                });
                
                return Number(counter * 100.0 / todo.tasks.length).toFixed(2);
            }
        },
        
        canComplete: function(todo) {
            var counter = 0;
            todo.tasks.forEach(task => {
                if (task.completed) {
                    counter += 1;
                }
            });
            
            return counter === todo.tasks.length;
        }
    }
});
