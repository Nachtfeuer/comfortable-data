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
let crudMixin = {
    methods: {
        /**
         * Read all todos from the service (backend) and update the UI.
         */
        readTodos: function() {
            axios.get('/todos').then(response => {
                if (response.status === 200) {
                    this.todos = [];
                    response.data.forEach(
                            entry => this.todos.push(this.convert2Frontend(entry)));
                }
            });            
        },

        /**
         * Creating the todo sending the data converted to the service (backend).
         */
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

        /**
         * Deleting given todo by id sending the request to the service (backend).
         *
         * @param {object} todo the ui todo structure.
         */
        deleteTodo: function (todo) {
            console.log("trying to delete \"" + todo.title + "\"");
            
            // You always can delete a todo but if you delete it and
            // the timer for it is running then it has to end.
            // No need to add working time for obvious reason.
            if (this.working.id === this.todo.id) {
                this.working = {id: undefined, start: undefined};
            }

            axios.delete('/todos/' + todo.id).then((response) => {
                if (response.status === 200) {
                    console.log("successfully deleted " + todo.title + "\"");
                    const pos = this.todos.findIndex(entry => entry.id === todo.id);
                    this.$delete(this.todos, pos);
                }
            });
        },

        /**
         * Updating the todo sending the todo converted to the service (backend).
         * The response contains the saved todo and will be used to update
         * the UI.
         */
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
         * Toggle completion state (sending the change to the service).
         *
         * @param {object} todo for which the completion state should be toggled.
         */
        toggleCompletion: function (todo) {
            this.todo = this.copyFrontend(todo);

            // completing a todo also the working on it should end
            // the working time should be added.
            if (!this.todo.completed && this.working.id === this.todo.id) {
                this.todo.workingTime += this.getWorkingTime();
                this.working = {id: undefined, start: undefined};
            }

            this.todo.completed = !this.todo.completed;
            this.updateTodo();
        }
    }
};
