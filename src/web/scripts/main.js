var wrapper = document.querySelector('.out-wrapper');
var vertical = document.querySelector('.wrapper-vertical');

function handleScroll() {
  var isAtBottom = wrapper.scrollTop + wrapper.clientHeight >= wrapper.scrollHeight;
  vertical.classList.toggle('vertical-scroller', isAtBottom);
}

wrapper.addEventListener('scroll', handleScroll);