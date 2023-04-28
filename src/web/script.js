var wrapper = document.querySelector('.out-wrapper');
var vertical = document.querySelector('.wrapper-vertical');
wrapper.addEventListener('scroll', function() {
    if (wrapper.scrollTop + wrapper.clientHeight >= wrapper.scrollHeight) {
            vertical.classList.add('vertical-scroller');

    } else if(vertical.classList.contains('vertical-scroller')){
        vertical.classList.remove('vertical-scroller');
    }

});