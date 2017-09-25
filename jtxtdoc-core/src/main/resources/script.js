var lastSelected = null;
function bindTOCToggle(){
    var toc = document.getElementById('toc');
    var list = toc.getElementsByTagName("div");
    for (var i in list){
        var div = list[i] 
    
       div.onclick = function(e){
            if (lastSelected !== null){
                lastSelected.classList.remove("selected");
            }
           this.classList.add("selected")  
           lastSelected = this;
         
        }
    }
    
}

(function() {


   // Feature Test
   if ( 'querySelector' in document && 'addEventListener' in window && Array.prototype.forEach ) {

       // Function to animate the scroll
       var smoothScroll = function (anchor, duration) {

           // Calculate how far and how fast to scroll
           var startLocation = window.pageYOffset;
           var offSet = document.body.getElementsByTagName("*")[0].offsetLeft - 120;
           var startXLocation = window.pageXOffset;
           var endLocation = anchor.offsetTop;
           var endXLocation = anchor.offsetLeft - offSet;
           
           var distance = endLocation - startLocation;
           var distanceX = endXLocation - startXLocation ;
           var increments = distance/(duration/16);
           var incrementsX = distanceX/(duration /16)
           var stopAnimation;
           var stopAnimationX;

           // Scroll the page by an increment, and check if it's time to stop
           var animateScroll = function () {
               window.scrollBy(0, increments);
               stopAnimation();

           };

           var animateScrollX = function () {
               window.scrollBy(incrementsX, 0);
               stopAnimationX();
           };

           
           // If scrolling down
           if ( increments >= 0 ) {
               // Stop animation when you reach the anchor OR the bottom of the
                // page
               stopAnimation = function () {
                   var travelled = window.pageYOffset;
                   if ( (travelled >= (endLocation - increments)) || ((window.innerHeight + travelled) >= document.body.offsetHeight) ) {
                       clearInterval(runAnimation);
                   }
               };
           } 
           // If scrolling up
           else {
               // Stop animation when you reach the anchor OR the top of the
                // page
               stopAnimation = function () {
                   var travelled = window.pageYOffset;
                   if ( travelled <= (endLocation || 0) ) {
                       clearInterval(runAnimation);
                   }
               };
   
           }
           
           // If scrolling right
           if ( incrementsX >= 0 ) {
               // Stop animation when you reach the anchor OR the bottom of the
                // page
               stopAnimationX = function () {
                   var travelled = window.pageXOffset;
                   if ( (travelled >= (endXLocation - incrementsX)) || ((window.innerWidth + travelled) >= document.body.scrollWidth) ) {
                       clearInterval(runAnimationX);
                   }
               };
           } else {   
               stopAnimationX = function () {
                   var travelled = window.pageXOffset;
                   if ( travelled <= (endXLocation || 0) ) {
                       clearInterval(runAnimationX);
                   }
               };
           }
           
          

           // Loop the animation function
           var runAnimation = setInterval(animateScroll, 16);
           var runAnimationX = setInterval(animateScrollX, 16);
      
       };

       // Define smooth scroll links
       var scrollToggle = document.querySelectorAll('.scroll');

       // For each smooth scroll link
       [].forEach.call(scrollToggle, function (toggle) {

           // When the smooth scroll link is clicked
           toggle.addEventListener('click', function(e) {

               // Prevent the default link behavior
               e.preventDefault();

               // Get anchor link and calculate distance from the top
               var dataID = toggle.getAttribute('href');
               var dataTarget = document.querySelector(dataID);
               var dataSpeed = toggle.getAttribute('data-speed');

               // If the anchor exists
               if (dataTarget) {
                   // Scroll to the anchor
                   smoothScroll(dataTarget, dataSpeed || 500);
               }

           }, false);

       });

   }

})();


//alternative to DOMContentLoaded
document.onreadystatechange = function () {
    if (document.readyState == "interactive") {
        bindTOCToggle();
        hljs.initHighlightingOnLoad();
//        document.getElementsByTagName("html")[0].className="compact";

    }
}
