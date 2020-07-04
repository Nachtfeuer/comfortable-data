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
function ChildModel(name, notCompletedCriteria, notCompletedTitle, allCritera, allTitle) {
    const self = this;
    self.name = name;
    self.notCompletedCriteria = notCompletedCriteria;
    self.notCompletedTitle = notCompletedTitle;
    self.allCriteria = allCritera;
    self.allTitle = allTitle;
}

function GroupModel(name) {
    const self = this;
    self.name = name;
    self.children = [];
    self.expanded = false;
    
    self.clear = function() {
        self.children = [];
    };

    /**
     * Adding a child to the given group.
     *
     * @param {string} name name of the child in the group
     * @param {function} notCompletedCriteria criteria for the category representing not completed todos.
     * @param {string} notCompletedTitle title/tooltip for not completed count of that category
     * @param {function} allCriteria criteria for the category representing all todos.
     * @param {string} allTitle title/tooltip for all of that category
     * @returns {GroupModel}
     */
    self.add = function(name, notCompletedCriteria, notCompletedTitle, allCriteria, allTitle) {
        self.children.push(new ChildModel(
                name,
                notCompletedCriteria, notCompletedTitle,
                allCriteria, allTitle));
        return self;
    };
}

let sidebarMixin = {
    data: {
        baseGroups: [],
        allGroups: []
    },

    created: function() {
        this.baseGroups.push(new GroupModel('Common Filters')
            .add('All',
                 this.isNotCompleted, "Displaying all not completed todos",
                 this.isAll, "Display all todos")
            .add('Completed',
                 undefined, '',
                 this.isCompleted, 'Displaying all completed todos')
            .add('High Priority',
                 this.isHighPriorityAndNotCompleted, 'Displaying all not completed todos with priority A',
                 this.isHighPriority, 'Displaying all todos with priority A')
            .add('Today',
                 undefined, '',
                 this.isToday, 'Displaying all todos of today')
            .add('No Estimation',
                 this.hasNoEstimation(this.isNotCompleted), 'Displaying all not completed todos with no estimation',
                 this.hasNoEstimation(), 'Displaying all todos with no estimation'));

        let tagGroup = new GroupModel('@Tags');
        this.baseGroups.push(tagGroup);

        let projectGroup = new GroupModel(':Projects');
        this.baseGroups.push(projectGroup);
    },
    
    computed: {
        /**
         * Provide list of groups and its children for the sidebar.
         *
         * @returns {Array|sidebarMixin.computed.getGroups.groups}
         */
        getGroups: function() {
            let pos = this.baseGroups.findIndex(group => group.name === '@Tags');
            this.baseGroups[pos].clear();
            
            this.getTags().forEach(tag => {
                this.baseGroups[pos].add(
                    tag,
                    this.hasTag(tag, this.isNotCompleted), 'All not completed todos tagged with ' + tag,
                    this.hasTag(tag), 'All todos tagged with ' + tag);
            });

            pos = this.baseGroups.findIndex(group => group.name === ':Projects');
            this.baseGroups[pos].clear();

            this.getProjects().forEach(project => {
                this.baseGroups[pos].add(
                    project,
                    this.hasProject(project, this.isNotCompleted), 'All not completed todos assign to project ' + project,
                    this.hasProject(project), 'All todos assigned to project ' + project);
            });

            return this.baseGroups;
        }
    },
    
    methods: {
        /**
         * Get unique list of assigned tags.
         *
         * @returns {Array} of tag names.
         */
        getTags: function () {
            let tags = [];
            
            this.todos.forEach(todo => {
                todo.tags.forEach(tag => {
                    if (tags.indexOf(tag) < 0) {
                        tags.push(tag);
                    }
                });
            });

            return tags.sort((tagA, tagB) => tagA.localeCompare(tagB));
        },

        /**
         * Get unique list of assigned projects.
         *
         * @returns {Array} of projects names.
         */
        getProjects: function () {
            let projects = [];
            
            this.todos.forEach(todo => {
                todo.projects.forEach(project => {
                    if (projects.indexOf(project) < 0) {
                        projects.push(project);
                    }
                });
            });

            return projects.sort((projectA, projectB) => projectA.localeCompare(projectB));
        }
    }
};
