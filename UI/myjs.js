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
  loadJSON('http://localhost:8080/total-percentage-people-in-db', (json) => {$('#tapudb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/current-percentage-people-in-db', (json) => {$('#capudb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/total-avg-age-in-db', (json) => {$('#taadb').text(Number(json).toFixed(0) + 'clk')});
  loadJSON('http://localhost:8080/current-avg-age-in-db', (json) => {$('#caadb').text(Number(json).toFixed(0) + 'clk')});
  loadJSON('http://localhost:8080/avg-time-to-find-friend', (json) => {$('#attff').text(Number(json).toFixed(0) + 'clk')});
  loadJSON('http://localhost:8080/total-percentage-recent-locations-in-db', (json) => {$('#tprldb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/current-percentage-recent-locations-in-db', (json) => {$('#cprldb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/max-current-percentage-recent-locations-in-db', (json) => {$('#maxcprldb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/min-current-percentage-recent-locations-in-db', (json) => {$('#mincprldb').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/current-percentage-people-out-of-range', (json) => {$('#cpoor').text(Number(json).toFixed(2) + '%')});
  loadJSON('http://localhost:8080/failed-friend-search', (json) => {$('#fs').text(Number(json).toFixed(2) + '')});
  loadJSON('http://localhost:8080/succeded-friend-search', (json) => {$('#ss').text(Number(json).toFixed(2) + '')});
  loadJSON('http://localhost:8080/event', (json) => {
    console.log(json);
    if(json){
      $('#events').html(json.description + ' </br> ' + $('#events').html());
    }
  });
}, 500);
