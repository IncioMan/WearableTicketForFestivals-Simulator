function getJSONP(url, success) {

    var ud = '_' + +new Date,
        script = document.createElement('script'),
        head = document.getElementsByTagName('head')[0]
               || document.documentElement;

    window[ud] = function(data) {
        head.removeChild(script);
        success && success(data);
    };

    script.src = url.replace('callback=?', 'callback=' + ud);
    head.appendChild(script);

}

window.setInterval(function(){
  loadJSON('http://localhost:8080/total-percentage-people-in-db', (json) => {$('#tapudb').text(json + '%')});
  loadJSON('http://localhost:8080/current-percentage-people-in-db', (json) => {$('#capudb').text(json + '%')});
  loadJSON('http://localhost:8080/total-avg-age-in-db', (json) => {$('#taadb').text(json + 's')});
  loadJSON('http://localhost:8080/current-avg-age-in-db', (json) => {$('#caadb').text(json + 's')});
  loadJSON('http://localhost:8080/total-percentage-recent-locations-in-db', (json) => {$('#tprldb').text(json + '%')});
  loadJSON('http://localhost:8080/current-percentage-recent-locations-in-db', (json) => {$('#cprldb').text(json + '%')});
  loadJSON('http://localhost:8080/current-percentage-people-out-of-range', (json) => {$('#cpoor').text(json + '%')});
}, 500);
