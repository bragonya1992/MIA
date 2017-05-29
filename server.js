var express = require('express');  
var app = express();  
var server = require('http').Server(app);  
var io = require('socket.io', { rememberTransport: false, transports: ['WebSocket', 'Flash Socket', 'AJAX long-polling'] })(server);
var DB =require('./moduloDB.js');
var keypress = require('keypress');
var contador=0;


app.use(express.static('/home/ubuntu/workspace'));

app.get('/', function(req, res) {  
  res.status(200).send("Hello World!");
});


io.sockets.on('connection', function(socket) {  
  socket.setMaxListeners(0);
  console.log('Alguien se ha conectado con Sockets '+socket.id+" username: "+socket.handshake.query.username);

  socket.on('app_user',function(cad){
  	var user = JSON.parse(cad);
  	DB.registrarSesion(user.username,user.keyChain,io.sockets.connected[this.id]);
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('listaCursosMaestro',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.username+" pidio su lista de cursos");
    DB.getCursosMaestro(peticion.username,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('enviarAsignacionCurso',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.username+" pidio asigrnarse el curso "+peticion.curso+" seccion: "+peticion.seccion);
    DB.asignarCurso(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

    socket.on('registrarUsuario',function(cad){
    var peticion = JSON.parse(cad);
    console.log(this.id+" pidio registrarse como "+peticion.role+" con nombre "+peticion.username);
    if(peticion.role=="maestro"){
      DB.registrarMaestro(peticion.username,peticion.pass,peticion.codigo,io.sockets.connected[this.id]);
    }else{
      DB.registrarAlumno(peticion.username,peticion.pass,peticion.codigo,io.sockets.connected[this.id]);
    }
    
  
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('listaCursosAlumno',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.username+" el alumno pidio su lista de cursos");
    DB.getCursosAlumno(peticion.username,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;
  
  socket.on('getAlumnos',function(cad){
    var peticion = JSON.parse(cad);
    console.log("se pidio la lista de alumnos de "+peticion.curso+" "+peticion.seccion);
    DB.getAlumnos(peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;
  
    socket.on('deleteSesion',function(cad){
      var peticion = JSON.parse(cad);
      console.log(peticion.username+" se eliminará su sesión ");
      DB.deleteSesion(peticion.username);
  
    }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('getMensajesAlumno',function(cad){
      var peticion = JSON.parse(cad);
      console.log(peticion.username+" pidio su mensajes de curso "+peticion.curso);
      DB.getMensajesAlumno(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;


  socket.on('getPublicacion',function(cad){
      var peticion = JSON.parse(cad);
      DB.getPublicacion(peticion.para,peticion.pagination,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('getLastPublicacion',function(cad){
      var peticion = JSON.parse(cad);
      DB.getLastPublicacion(peticion.para,peticion.lastId,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('authPublication',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.codigo+" pidio autorizacion para publicar ");
    DB.authPublication(peticion.codigo,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

  socket.on('publicar',function(cad){
    var peticion = JSON.parse(cad); // here is my workspace
    console.log(peticion.codigo+" (super usuario) va a publicar ");
    DB.publicar(peticion.codigo,peticion.para,peticion.contenido,io.sockets.connected[this.id],io.sockets);
  
  
  }).on('error', function(err) { console.log("handler error" +err) });;


  socket.on('getMensajesMaestro',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.username+" pidio su mensajes de su curso curso "+peticion.curso);
    DB.getMensajesMaestro(peticion.username,peticion.curso,peticion.seccion,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;


  socket.on('getTopMaestro',function(cad){
    var peticion = JSON.parse(cad);
    console.log(peticion.username+" pidio top de su curso curso "+peticion.curso);
    DB.getTopMaestro(peticion.username,peticion.curso,peticion.seccion,peticion.inicio,peticion.final,io.sockets.connected[this.id]);
  
  }).on('error', function(err) { console.log("handler error" +err) });;

    socket.on('cambiarVisibilidad',function(cad){
    var peticion = JSON.parse(cad);
    DB.cambiarVisibilidad(peticion.username,peticion.curso,peticion.seccion);

  
  }).on('error', function(err) { console.log("handler error" +err) });;

    socket.on('getTopAlumno',function(cad){
    var peticion = JSON.parse(cad);
    DB.getTopAlumno(peticion.username,peticion.curso,peticion.seccion,peticion.inicio,peticion.final,io.sockets.connected[this.id]);
  }).on('error', function(err) { console.log("handler error" +err) });


  socket.on('autenticar',function(cad){
    var peticion = JSON.parse(cad);
      console.log("Quiere autenticarse: "+cad);
      DB.autenticar(peticion.carne,peticion.pass,peticion.role,io.sockets.connected[this.id]);
  }).on('error', function(err) { console.log("handler error" +err) });

  socket.on('sendMessage',function(cad){
    var peticion = JSON.parse(cad);
    console.log("El maestro quiere enviar un mensaje: "+cad);
    DB.insertMensajeMaestro(peticion.username,peticion.curso,peticion.seccion,peticion.mensaje,io.sockets);
  
  }).on('error', function(err) { console.log("handler error" +err) });

  socket.on('getListadoCursos',function(){
      DB.getListadoCursos(io.sockets.connected[this.id]);
  }).on('error', function(err) { console.log("handler error" +err) });

  socket.on('disconnect',function(user){
    
  }).on('error', function(err) { console.log("handler error" +err) });;

  
}).on('error', function(err) { console.log("handler error" +err) });







server.listen(8080, function() {  
  console.log("Servidor corriendo en http://localhost:8080");
}).on('error', function(err) { console.log("handler error" +err) });;