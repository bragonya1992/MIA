var express = require('express');  
var app = express();  
var server = require('http').Server(app);  
var io = require('socket.io', { rememberTransport: false, transports: ['WebSocket', 'Flash Socket', 'AJAX long-polling'] })(server);
var DB =require('./moduloDB.js');
var app_users=[];
var keypress = require('keypress');
var contador=0;


app.use(express.static('/home/ubuntu/workspace'));

app.get('/', function(req, res) {  
  res.status(200).send("Hello World!");
});



// make `process.stdin` begin emitting "keypress" events
keypress(process.stdin);

//listen for the "keypress" event
/*process.stdin.on('keypress', function (ch, key) {
  console.log('got "keypress"', key);
  if (key.ctrl && key.name == 'x') {
    process.exit();
  }else if(key.ctrl && key.name == 'p'){
    for(var socketid in app_users){
    console.log("Usuario: "+app_users[socketid].username+" con socket: "+socketid);
  }
  console.log("pp");
  }
});*/

// process.stdin.setRawMode(true);
// process.stdin.resume();

io.sockets.on('connection', function(socket) {  
  console.log('Alguien se ha conectado con Sockets '+socket.id+" username: "+socket.handshake.query.username);
  console.log("Users: ", app_users);
  if(socket.handshake.query.username){
      var user = JSON.parse("{\"username\":\""+socket.handshake.query.username+"\",\"role\":\""+socket.handshake.query.role+"\"}");
      app_users[socket.id]=user;
      console.log("Usuario registrado Query: "+user.username+" socket "+socket.id);
      if(user.role.toLowerCase()=="1"){
        DB.notificarAlumnos(user.username,socket);
      }else{
        console.log('no notification for teacher, only notices');
      }
    }else{
      console.log("no register socket, identity anonymous "+socket.id);
    }
  
  
/*  io.use(function(socket, next){
    console.log("Users: ", app_users);
    if(socket.handshake.query.username){
      var user = JSON.parse("{\"username\":\""+socket.handshake.query.username+"\",\"role\":\""+socket.handshake.query.role+"\"}");
      app_users[socket.id]=user;
      console.log("Usuario registrado Query: "+user.username+" socket "+socket.id);
      if(user.role.toLowerCase()=="1"){
        DB.notificarAlumnos(user.username,socket);
      }else{
        console.log('no notification for teacher, only notices');
      }
    }else{
      console.log("no register socket, identity anonymous "+socket.id);
    }
    next();
}).on('error', function(err) { console.log("handler error" +err) });*/

  socket.on('app_user',function(cad){
  	var user = JSON.parse(cad);
  	app_users[this.id]=user;
  	console.log("Usuario registrado: "+user.username+" socket "+this.id);
    if(user.role.toLowerCase()=="1"){
      DB.notificarAlumnos(user.username,io.sockets.connected[this.id]);
    }else{
      console.log('no notification for teacher, only notices');
    }
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
    }else{
      console.log("no se encontro socket "+this.id);
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
  
  socket.on('getAlumnos',function(cad){
    if(app_users[this.id]){
    var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" el alumno pidio su lista de cursos");
      DB.getAlumnos(peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
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


  socket.on('getPublicacion',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" pidio sus publicaciones ");
      DB.getPublicacion(peticion.para,peticion.pagination,io.sockets.connected[this.id]);
    }
  
  });

  socket.on('getLastPublicacion',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" pidio sus publicaciones ");
      DB.getLastPublicacion(peticion.para,peticion.lastId,io.sockets.connected[this.id]);
    }
  
  });

  socket.on('authPublication',function(cad){
    console.log("alguien quiere publicar PROCEDURE");
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" pidio autorizacion para publicar ");
      DB.authPublication(peticion.codigo,io.sockets.connected[this.id]);
    }
  
  });

  socket.on('publicar',function(cad){
    if(app_users[this.id]){
      var peticion = JSON.parse(cad);
      console.log(app_users[this.id].username+" (super usuario) va a publicar ");
      DB.publicar(peticion.codigo,peticion.para,peticion.contenido,io.sockets.connected[this.id],app_users,io.sockets);
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







server.listen(8080, function() {  
  console.log("Servidor corriendo en http://localhost:8085");
});