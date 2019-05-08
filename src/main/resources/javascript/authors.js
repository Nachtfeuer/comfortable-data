/*
 * The MIT License
 *
 * Copyright 2019 Thomas Lehmann.
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
var app = new Vue({
    el: '#content',
    data: {
        search: '',
        authors: [],
        fullNameState: null,
        new_author: {
            fullName: ''
        }
    },

    computed: {
        filteredAuthors() {
            var searchText = this.search.toLowerCase();
            return this.authors.filter( author => {
                return author.fullName.toLowerCase().includes(searchText);
            });
        }
    },

    methods: {
        resetAddAuthor(event) {
            this.new_author.fullName = '';
            this.fullNameState = null;
        },

        handleAddAuthor(event) {
            if (this.new_author.fullName.length > 0) {
                axios
                    .post('/books/authors', this.new_author)
                    .then(response => {
                        this.loadAuthors()
                    });
            } else {
                event.preventDefault();
                this.fullNameState = 'invalid';
            }
        },

        loadAuthors() {
            axios
                .get('/books/authors')
                .then(response => (this.authors = response.data));
        }
    },

    mounted() {
        this.loadAuthors();
        if (localStorage.authorSearch) {
            this.search = localStorage.authorSearch;
        }
    },

    watch: {
        search(newSearch) {
            localStorage.authorSearch = newSearch;
        }
    }
});
