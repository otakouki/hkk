window.addEventListener('DOMContentLoaded', () => {
    {
        'use strict'

        class SlideModel {

            constructor(attr) {
                this.attr = {
                    offset: attr.offset,
                    length: attr.length
                }

                this.listeners = {
                    update: null
                }
            }

            on(event, func) {
                this.listeners[event] = func;
            }

            trigger(event) {
                this.listeners[event]();
            }

            next(offset) {
                this.attr.offset += offset;
                if (this.attr.offset >= this.attr.length) {
                    this.attr.offset = this.attr.length - 1;
                }
                this.trigger('update');
            }

            prev(offset) {
                this.attr.offset -= offset;
                if (this.attr.offset <= 0) {
                    this.attr.offset = 0;
                }
                this.trigger('update');
            }

            autoSlide() {
                setInterval(() => {
                    if (this.attr.offset >= this.attr.length - 1) {
                        this.attr.offset = -1;
                    }
                    this.next(1);
                }, 8000);
            }

        }

        class SlideView {

            constructor(el) {
                this.el = el;
                this.init();
                this.model.autoSlide();
            }

            cursorAutoHidden() {
                this.el.classList.remove('hidden-cursor');
                clearTimeout(this.timer);
                this.timer = setTimeout(() => {
                    this.el.classList.add('hidden-cursor');
                }, 3000);
            }

            init() {
                this.slide_content = this.el.querySelectorAll('.slide-content');

                const obj = {
                    offset: 0,
                    length: this.slide_content.length
                }
                this.model = new SlideModel(obj);

                this.model.on('update', () => {
                    this.update();
                });
                this.update();

                document.addEventListener('keyup', e => {
                    switch (e) {
                        case 37:
                            this.model.prev(1);
                            break;
                        case 39:
                            this.model.next(1);
                            break;
                    }
                });

                document.addEventListener('click', e => {
                    this.model.next(1);
                });

                document.addEventListener('contextmenu', e => {
                    this.model.prev(1);
                });

                this.el.addEventListener('mousemove', () => {
                    this.cursorAutoHidden();
                });
            }

            update() {
                for (let i = 0, len = this.slide_content.length; i < len; i++) {
                    this.slide_content[i].style.display = 'none';
                }

                setTimeout(() => {
                    this.slide_content[this.model.attr.offset].style.display = '';
                }, 500);
            }

        }

        const slide = document.querySelector('.slide');
        const slide_view = new SlideView(slide);

    }
});

