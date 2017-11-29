var bubbles = [];
var person_width = 40;
var person_height = 40;
var guests = [];

class Person{

  constructor(id, X, Y, broadcasting){
    this.id = id;
    this.X = X;
    this.Y = Y;
    this.broadcasting = broadcasting;
    this.rangeRadius = 300;
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
      if(this.broadcasting){
        noFill();
        stroke('#40C4FF');
        this.rangeCounter += 2;
        this.rangeCounter = this.rangeCounter % this.rangeRadius;
        if(this.rangeCounter < person_width/2){
          this.rangeCounter = person_width * 2;
        }
        ellipse(this.X,this.Y,this.rangeCounter/2,this.rangeCounter/2);
        ellipse(this.X,this.Y,this.rangeCounter,this.rangeCounter);
        ellipse(this.X,this.Y,this.rangeRadius,this.rangeRadius);
      }
    }

    clicked(){
      var d = dist(mouseX, mouseY, this.X, this.Y);
      if(d < person_width){
        alert("You clicked me at " + this.X);
      }
    }
}

function setup() {
  var canvas = createCanvas(1200, 600);
  //guests.push(new Person(0,100,100, true));
  // Move the canvas so it's inside our <div id="sketch-holder">.
  canvas.parent('sketch-holder');
  noStroke();
}

function draw(){
  background('#A5D6A7');
  guests.forEach(function(g){
      g.show();
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
}, 100);

function drawGuests(people) {
  guests = [];
  people.forEach(function(p){
      guests.push(new Person(p.id, p.position.coordinates.x, p.position.coordinates.y, random(1) > 0));
  });
}
