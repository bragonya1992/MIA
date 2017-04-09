var express = require('express');  
var app = express();  
var server = require('http').Server(app);  
var io = require('socket.io')(server);
var DB =require('./moduloDB.js');
var app_users=[];
var keypress = require('keypress');
var contador=0;


app.use(express.static('C:\\Users\\Brayan\\Documents\\node\\sala de chat'));

app.get('/', function(req, res) {  
  res.status(200).send("Hello World!");
});



// make `process.stdin` begin emitting "keypress" events
keypress(process.stdin);

//listen for the "keypress" event
process.stdin.on('keypress', function (ch, key) {
  console.log('got "keypress"', key);
  if (key.ctrl && key.name == 'x') {
    process.exit();
  }else if(key.ctrl && key.name == 'p'){
    for(var socketid in app_users){
    console.log("Usuario: "+app_users[socketid].username+" con socket: "+socketid);
  }
  console.log("pp");
  }
});

// process.stdin.setRawMode(true);
// process.stdin.resume();

io.sockets.on('connection', function(socket) {  
  console.log('Alguien se ha conectado con Sockets 1'+socket.id);
  socket.on('app_user',function(cad){
  	var user = JSON.parse(cad);
  	app_users[this.id]=user;
  	console.log("Usuario registrado: "+user.username);
  //   for(var socketid in app_users){
  //     if(app_users[socketid].username=="201213578"){
  //   console.log("lista: "+app_users[socketid].username);
  //   io.sockets.in(socketid).emit("inbox","{\"arreglo\":[{\"seccion\":\"A\",\"visibilidad\":\"1\",\"curso\":\"Mate1\",\"catedratico\":\"Zambrano\",\"mensaje\":\"Buenas noches jovenes\"},{\"seccion\":\"B\",\"curso\":\"Ortodoncia 1\",\"mensaje\":\"manana materiales\"},{\"seccion\":\"C\",\"curso\":\"Progra1\",\"mensaje\":\"Llevar computadora\"}]}");
  //   console.log("acabo de enviar:   "+"{\"arreglo\":[{\"seccion\":\"A\",\"visibilidad\":\"1\",\"curso\":\"Mate1\",\"mensaje\":\"Buenas noches jovenes\"},{\"seccion\":\"B\",\"curso\":\"Ortodoncia 1\",\"mensaje\":\"manana materiales\"},{\"seccion\":\"C\",\"curso\":\"Progra1\",\"mensaje\":\"Llevar computadora\"}]}");
  // }}
  });

  socket.on('listaCursosMaestro',function(cad){
    var peticion = JSON.parse(cad);
    if(app_users[this.id]){
      console.log(app_users[this.id].username+" pidio su lista de cursos");
      DB.getCursosMaestro(peticion.username,io.sockets.connected[this.id]);
    }
  
  });

  socket.on('enviarAsignacionCurso',function(cad){
    var peticion = JSON.parse(cad);
    if(app_users[this.id]){
      console.log(app_users[this.id].username+" pidio asigrnarse el curso "+peticion.curso+" seccion: "+peticion.seccion);
      DB.asignarCurso(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
    }
  
  });

    socket.on('registrarUsuario',function(cad){
    var peticion = JSON.parse(cad);
    console.log(this.id+" pidio registrarse como "+peticion.role+" con nombre "+peticion.username);
    if(peticion.role=="maestro"){
      DB.registrarMaestro(peticion.username,peticion.pass,peticion.codigo,io.sockets.connected[this.id]);
    }else{
      DB.registrarAlumno(peticion.username,peticion.pass,peticion.codigo,io.sockets.connected[this.id]);
    }
    
  
  });

  socket.on('listaCursosAlumno',function(cad){
    if(app_users[this.id]){
    var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" el alumno pidio su lista de cursos");
      DB.getCursosAlumno(peticion.username,io.sockets.connected[this.id]);
    }
  
  });


  socket.on('getMensajesAlumno',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log("JSON: "+cad);
      console.log(app_users[this.id].username+" pidio su mensajes de curso "+peticion.curso);
      DB.getMensajesAlumno(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
    }
  
  });


  socket.on('getMensajesMaestro',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log("JSON: "+cad);
      console.log(app_users[this.id].username+" pidio su mensajes de su curso curso "+peticion.curso);
      DB.getMensajesMaestro(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
    }
  
  });


  socket.on('getTopMaestro',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log("JSON: "+cad);
      console.log(app_users[this.id].username+" pidio top de su curso curso "+peticion.curso);
      DB.getTopMaestro(peticion.username,peticion.curso,peticion.seccion,peticion.inicio,peticion.final,io.sockets.connected[this.id]);
    }
  
  });

    socket.on('cambiarVisibilidad',function(cad){
    var peticion = JSON.parse(cad);
    DB.cambiarVisibilidad(peticion.username,peticion.curso,peticion.seccion);

  
  });

    socket.on('getTopAlumno',function(cad){
    var peticion = JSON.parse(cad);
        if(app_users[this.id]){
          console.log("inicio: "+peticion.inicio+" final: "+peticion.final);
          DB.getTopAlumno(peticion.username,peticion.curso,peticion.seccion,peticion.inicio,peticion.final,io.sockets.connected[this.id]);
        }
  });


  socket.on('autenticar',function(cad){
    var peticion = JSON.parse(cad);
    console.log("Quiere autenticarse: "+cad);
    DB.autenticar(peticion.carne,peticion.pass,peticion.role,io.sockets.connected[this.id]);
  });

  socket.on('sendMessage',function(cad){
    var peticion = JSON.parse(cad);
    console.log("El maestro quiere enviar un mensaje: "+cad);
    DB.insertMensajeMaestro(peticion.username,peticion.curso,peticion.seccion,peticion.mensaje,io.sockets,app_users);
  
  });

  socket.on('getListadoCursos',function(){
    if(app_users[this.id]){
      DB.getListadoCursos(io.sockets.connected[this.id]);
    }
    
  
  });

  socket.on('disconnect',function(user){
    if(app_users[this.id]){
    	console.log("se ha eliminado a "+app_users[this.id].username)
    	delete app_users[this.id];
      console.log("usuarios online: "+app_users.length);
    }else{
      console.log("Socket anonimo: "+this.id+" acaba de abandonar");
    }
  });

  
});







server.listen(8081, function() {  
  console.log("Servidor corriendo en http://localhost:8080");
});
