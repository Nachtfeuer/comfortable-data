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
        sortBy: 'title',
        sortDesc: false,
        search: '',
        fields: [
            {key: 'index', label: '#'},
            {key: 'title', label: 'Title', sortable: true},
            {key: 'authors', label: 'Authors', sortable: true},
            {key: 'publisher', label: 'Publisher'},
            {key: 'yearOfPublication', label: 'Year'},
            {key: 'pages', label: 'Pages', sortable: true},
            {key: 'rating', label: 'Rating', sortable: true},
            {key: 'tags', label: 'Tags', sortable: true}
        ],
        books: []
    },

    mounted() {
        axios
            .get('/books')
            .then(response => (this.books = response.data));

        if (localStorage.bookSearch) {
            this.search = localStorage.bookSearch;
        }
    },
    
    watch: {
        search(newSearch) {
            localStorage.bookSearch = newSearch;
        }
    },
    
    methods: {
        filterBook(book) {
            console.log(book)
            var searchText = this.search.toLowerCase();
            return book.title.toLowerCase().includes(searchText) ||
                book.authors.filter(author => {
                    return author.fullName.toLowerCase().includes(searchText);
                }).length > 0 ||
                book.publisher.fullName.toLowerCase().includes(searchText) ||
                (book.rating && book.rating.toLowerCase().includes(searchText)) ||
                (book.tags && book.tags.filter(tag => {
                    return tag.name.toLowerCase().includes(searchText);
                }).length > 0);
        }
    }
});
