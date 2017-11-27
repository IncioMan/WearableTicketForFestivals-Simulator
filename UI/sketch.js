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
        ellipse(this.X,this.Y,person_width*3,person_height*3);
        ellipse(this.X,this.Y,person_width*6,person_width*6);
        ellipse(this.X,this.Y,person_width*9,person_width*9);
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
  createCanvas(1200, 600);
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
}, 90);

function drawGuests(people) {
  guests = [];
  people.forEach(function(p){
      guests.push(new Person(p.id, p.position.coordinates.x, p.position.coordinates.y, random(1) > .5));
  });
}
