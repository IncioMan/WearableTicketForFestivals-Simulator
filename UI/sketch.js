var bubbles = [];
var person_width = 12;
var person_height = 12;
var guests = [];
var concerts = [];
var concertImg;

class Concert{

  constructor(X, Y, logo){
    this.X = X;
    this.Y = Y;
    this.logo = logo;
  }

  show(){
//      if(this.logo != null){
//        image(this.logo, this.X-50, this.Y-100, 100, 100);
//      }else{
         stroke('black');
         fill('#9E9E9E');
         rect(this.X-50, this.Y-100, 100, 50);
//       }
  }
}

class Person{

  constructor(id, X, Y, range, communicating, listening, responsing, requesting){
    this.id = id;
    this.X = X;
    this.Y = Y;
    this.communicating = communicating;
    this.requesting = requesting;
    this.listening = listening;
    this.responsing = responsing;
    this.rangeRadius = range;
    this.rangeCounter = 0;
    this.col = 'white';
  }

  show(){
      stroke('black');
      fill(this.col);
      ellipse(this.X,this.Y,person_width,person_height);
      noStroke();
      fill('black');
      textSize(20);
      text(this.id, this.X - person_width/4, this.Y - person_height/4, 20, 20);
      noFill();
      stroke('black');
      if(this.communicating){
        stroke('#40C4FF');
      }
      if(this.requesting){
        stroke('red');
      }
      if(this.responsing){
        stroke('green');
      }
      if(this.listening){
        stroke('yellow');
      }
      //this.rangeCounter += 2;
      //this.rangeCounter = this.rangeCounter % this.rangeRadius;
      //if(this.rangeCounter < person_width/2){
        //this.rangeCounter = person_width * 2;
      //
      //ellipse(this.X,this.Y,this.rangeCounter/2,this.rangeCounter/2);
      //ellipse(this.X,this.Y,this.rangeCounter,this.rangeCounter);
      ellipse(this.X,this.Y,this.rangeRadius*2,this.rangeRadius*2);
    }

    clicked(){
      var d = dist(mouseX, mouseY, this.X, this.Y);
      if(d < person_width){
        alert("You clicked me at " + this.X);
      }
    }
}

function setup() {
  var canvas = createCanvas(1000, 500);
  //guests.push(new Person(0,100,100, true));
  // Move the canvas so it's inside our <div id="sketch-holder">.
  canvas.parent('sketch-holder');
  noStroke();
  concertImg = loadImage("https://d30y9cdsu7xlg0.cloudfront.net/png/106417-200.png");

  // for(var i = 0; i < 800; i++){
  //   guests.push(new Person(i, random(1200), random(800) + 200, 55, random(1) > .5));
  // }
}

function draw(){
  background('#A5D6A7');
  guests.forEach(function(g){
      g.show();
  });
  concerts.forEach(function(c){
      c.show();
  });
}

function mousePressed(){
  guests.forEach(function(g){
      g.clicked();
  });
}

window.setInterval(function(){
  var url = 'http://localhost:8080/guests'
  loadJSON(url, drawGuests);
  if(concerts.length == 0){
    var url = 'http://localhost:8080/concerts'
    loadJSON(url, drawConcerts);
  }
}, 100);

function drawGuests(people) {
  guests = [];
  people.forEach(function(p){
      guests.push(new Person(p.id, p.position.coordinates.x, p.position.coordinates.y, p.range, p.communicating, p.listening, p.responsing, p.requesting));
  });
}

function drawConcerts(cs) {
  concerts = [];
  cs.forEach(function(c){
      concerts.push(new Concert(c.concertLocation.coordinates.x, c.concertLocation.coordinates.y, concertImg));
  });
}
