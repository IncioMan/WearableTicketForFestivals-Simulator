var bubbles = [];
var person_width = 10;
var person_height = 10;
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
      if(this.logo != null){
        image(this.logo, this.X, this.Y, 100, 100);
      }else{
        fill('#9E9E9E');
        rect(this.X, this.Y, 100, 50);
      }
  }
}

class Person{

  constructor(id, X, Y, range, communicating){
    this.id = id;
    this.X = X;
    this.Y = Y;
    this.communicating = communicating;
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
      text(this.id, this.X - person_width/4, this.Y - person_height/4, person_width, person_height);
      noFill();
      if(this.communicating){
        stroke('#40C4FF');
      }else{
        stroke('black');
      }
      //this.rangeCounter += 2;
      //this.rangeCounter = this.rangeCounter % this.rangeRadius;
      //if(this.rangeCounter < person_width/2){
        //this.rangeCounter = person_width * 2;
      //
      //ellipse(this.X,this.Y,this.rangeCounter/2,this.rangeCounter/2);
      //ellipse(this.X,this.Y,this.rangeCounter,this.rangeCounter);
      ellipse(this.X,this.Y,this.rangeRadius,this.rangeRadius);
    }

    clicked(){
      var d = dist(mouseX, mouseY, this.X, this.Y);
      if(d < person_width){
        alert("You clicked me at " + this.X);
      }
    }
}

function setup() {
  var canvas = createCanvas(1200, 800);
  //guests.push(new Person(0,100,100, true));
  // Move the canvas so it's inside our <div id="sketch-holder">.
  canvas.parent('sketch-holder');
  noStroke();
  concertImg = loadImage("stage.png");

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
  if(concerts == null){
    var url = 'http://localhost:8080/concerts'
    loadJSON(url, drawConcerts);
  }
}, 300);

function drawGuests(people) {
  guests = [];
  people.forEach(function(p){
      guests.push(new Person(p.id, p.position.coordinates.x, p.position.coordinates.y, p.range, p.communicating));
  });
}

function drawConcerts(cs) {
  concerts = [];
  cs.forEach(function(c){
      concerts.push(new Concert(c.concertLocation.coordinates.x, c.concertLocation.coordinates.y, concertImg));
  });
}
