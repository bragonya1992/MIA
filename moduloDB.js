var semestreActual=2;
var date = new Date();
var anioActual = 2016;//date.getFullYear();
var mysql      = require('mysql');
var credentials =require('./local.js');
var FCM = require('fcm-node');
var oneSignal = require('onesignal')('ZDA1ZTU4NDUtNjFjZC00ZTFhLWJiMGEtMzdlZGYyNjlmNjkz', '606aa01a-676b-4a6c-89da-37da13078997', true);
var serverKey = 'AAAALWe_bTo:APA91bHzwpdBtswfdrkov_6_OCHddTgFubCkfKEwg5P51En4yvpWio8eToTHXb0spI-SGv1VSs53O6qteEPZ1Gxg6mUuqFii0uetSbrxgDKlPD8ekNjiJjlbNxF39EdtxIFCVU_X2DQv'; //put your server key here 
var fcm = new FCM(serverKey);
  var connection = mysql.createConnection({
    host     : credentials.hostDB/*'localhost'*/,
    user     : credentials.userDB/*'root'*/,
    password : credentials.passwordDB,
    database : credentials.databaseName/*'nodeprueba2'*/
  });

  connection.connect();

exports.query=function(callback,nombre){
  connection.query('SELECT * from usuario where nombre=?',[nombre], function(err, rows, fields) {
    if (!err){
      console.log('The solution is: ', JSON.stringify(rows));
      callback(JSON.stringify(rows));
    }
    else{
      console.log('Error while performing Query.'+err);
      callback(err);
    }
  });
}

function sendRealTimeOneSignal(keys, collapse,mensaje,cuerpo, titulo,tipo,curso,seccion,fecha,para,idPublicacion,title){
  
  var  data = {  //you can send only notification or only data(or include both) 
          type: tipo,
          curse: curso,
          section: seccion,
          content: mensaje,
          date: fecha,
          to: para,
          publication: idPublicacion,
          tituloPublication:title
      };
      if(tipo=="notification"){
        oneSignal.createNotification(mensaje,data, keys,curso,seccion)
      }else{
        oneSignal.createNotification(mensaje,data,keys,tipo,"")
      }
}


exports.autenticar=function(carne,pass,role,socket){
  var tabla ="Alumno"; var campo1 ="Carne"; var rol =1;
  if(role=="Maestro"){
    var tabla ="Maestro";
    var campo1="CodigoMaestro";
    var rol =2;
  }
  var notes;
  connection.query('SELECT * from '+tabla+' where '+campo1+'=? and Contrasena=?',[carne,pass], function(err, rows, fields) {
    if (!err){
      console.log('The solution is: ', JSON.stringify(rows));
      if(rows.length>0){
        if(rol!=2){
          notes="{\"nombre\":\""+rows[0].Nombre+"\",\"carne\":\""+rows[0].Carne+"\",\"role\":\""+rol+"\"}";
        }else{
          notes="{\"nombre\":\""+rows[0].Nombre+"\",\"carne\":\""+rows[0].CodigoMaestro+"\",\"role\":\""+rol+"\"}";
        }
      }else{
        notes="{\"error\":\"Sus datos son invalidos, por favor vuelva a intentarlo\"}";
      }
    }
    else{
      console.log('Error while performing Query.'+err);
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on picker: "+notes);
              if(socket){
                socket.emit("responseAutenticar", notes);
              }
            });
}


exports.getCursosMaestro=function(CodigoMaestro,socket){
  var notes;
  connection.query(`select Curso.Nombre As Nombre,Maestro.Nombre As Catedratico,AsignacionMaestro.fkSeccion as Seccion from AsignacionMaestro 
join Curso on fkCodigoCurso=CodigoCurso 
join Maestro on CodigoMaestro=fkCodigoMaestro 
where Maestro.CodigoMaestro=? and AsignacionMaestro.fkSemestre=? and AsignacionMaestro.fkAnio=?`,[CodigoMaestro,semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      //console.log('The solution is: ', JSON.stringify(rows));
      console.log("EXTRACCION: ");
      notes="{\"cursos\":[";
      for(var i in rows){
        notes+="{\"nombre\":\""+rows[i].Nombre+"\",\"catedratico\":\""+rows[i].Catedratico+"\",\"seccion\":\""+rows[i].Seccion+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on: "+notes);

              console.log("ENVIANDO A MAESTRO ---------------------"+CodigoMaestro);
              console.log("salida on picker 2: "+notes);
                socket.emit("recibirCursos", notes);
            });
}

exports.getCursosAlumno=function(carne,socket){
  console.log("Extrallendo cursos de "+carne);
  var notes;
  connection.query(`select Curso.Nombre,AsignacionAlumno.fkSeccion as Seccion, (select COUNT(*) from Instancia
where fkCodigoCursoAlumno=AsignacionAlumno.fkCodigoCurso and fkCarne=AsignacionAlumno.fkCarne and Instancia.fkSeccionAlumno=AsignacionAlumno.fkSeccion and fkSemestreAlumno=AsignacionAlumno.fkSemestre and fkAnioAlumno=AsignacionAlumno.fkAnio and visto=0) as Contador
from AsignacionAlumno 
join Curso on fkCodigoCurso=CodigoCurso
where AsignacionAlumno.fkCarne=? and fkSemestre=? and fkAnio=?
group by Curso.Nombre,  Seccion`,[carne,semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      //console.log('The solution is: ', JSON.stringify(rows));
      //console.log("EXTRACCION: "+rows[0].Nombre);
      notes="{\"cursos\":[";
      for(var i in rows){
        notes+="{\"nombre\":\""+rows[i].Nombre+"\",\"catedratico\":\"\",\"seccion\":\""+rows[i].Seccion+"\",\"contador\":\""+rows[i].Contador+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              //console.log("salida on: "+notes);
              console.log("ENVIANDO A MAESTRO ---------------------"+carne);
                socket.emit("recibirCursos", notes);
            });
}

exports.insertMensajeMaestro=function(username,curso,seccion,mensaje,socket){
  var notes;
  connection.query(`insert into Mensaje(idMensaje,fkCodigoMaestro,fkCodigoCurso,fkSeccion,fkSemestre,fkAnio,mensaje) values(0,?,(select CodigoCurso
from Curso where Nombre=?),?,?,?,?);`,[username,curso,seccion,semestreActual,anioActual,mensaje], function(err, rows, fields) {
    if (!err){
      notes=1;
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              console.log("salida on insertMensaje: "+notes);
              if(notes==1){
                listaAlumnos(curso,seccion,mensaje,socket);
              }
            });
}


exports.publicar=function(CodigoMaestro,para,contenido,titulo,socket,socketon){
  var notes;
  connection.query(`insert into Publicacion(idPublicacion,para,fkCodigoMaestro,contenido,titulo) values(0,?,?,?,?);`,[para,CodigoMaestro,contenido,titulo], function(err, rows, fields) {
    if (!err){
      notes = rows;
      notes="{\"publicacion\":[";
      notes+="{\"idPublicacion\":\""+rows.insertId+"\",\"fecha\":\"hace pocos momentos\",\"titulo\":\""+titulo+"\",\"contenido\":\""+contenido+"\",\"para\":\""+para+"\"}";
      notes+="]}";
      //console.log("ESTAS SON MIS NOTAS: "+JSON.stringify(fields));
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              console.log("salida on publicacion: "+notes);
              if(notes!=0){
                socket.emit("responsePublicacion","Su ultima publicacion fue exitosa");
                if(para==1){
                  notificarTodosAlumnos(notes,socketon);
                }else if(para ==2){
                  notificarTodosMaestro(notes,socketon);
                }else if(para ==0){
                  notificarTodosMaestro(notes,socketon);
                  notificarTodosAlumnos(notes,socketon);
                }
              }else{
                socket.emit("responsePublicacion","Su ultima publicacion fue erronea, lo sentimos");
              }
            });
}

exports.getPublicacion=function(para,pagination,socket){
  var notes;
  var realPaginationInf = pagination*10;
  connection.query(`select idPublicacion,DATE_FORMAT(fecha,'%Y-%m-%d %H:%i') As fecha, contenido, para,titulo from Publicacion where para=0 or para=? order by fecha desc limit ?,10;`,[para,realPaginationInf], function(err, rows, fields) {
    if (!err){
      notes="{\"publicacion\":[";
      for(var i in rows){
        if(rows[i].titulo==undefined || rows[i].titulo==null || rows[i].titulo=="" ){
          notes+="{\"idPublicacion\":\""+rows[i].idPublicacion+"\",\"fecha\":\""+rows[i].fecha+"\",\"titulo\":\"\",\"contenido\":\""+rows[i].contenido+"\",\"para\":\""+rows[i].para+"\"},";
        }else{
          notes+="{\"idPublicacion\":\""+rows[i].idPublicacion+"\",\"fecha\":\""+rows[i].fecha+"\",\"titulo\":\""+rows[i].titulo+"\",\"contenido\":\""+rows[i].contenido+"\",\"para\":\""+rows[i].para+"\"},";
        }
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              console.log("salida on getpublicacion: "+notes);
              socket.emit("recieverPublications",notes);
            });
}


exports.getLastPublicacion=function(para,lastId,socket){
  var notes;
  var lengthRows=0;
  connection.query(`select idPublicacion,DATE_FORMAT(fecha,'%Y-%m-%d %H:%i') As fecha, contenido, para from Publicacion where (para=0 or para=?) and idPublicacion>? order by fecha desc limit 0,10;`,[para,lastId], function(err, rows, fields) {
    if (!err){
      lengthRows=rows.length;
      notes="{\"publicacion\":[";
      for(var i in rows){
        notes+="{\"idPublicacion\":\""+rows[i].idPublicacion+"\",\"fecha\":\""+rows[i].fecha+"\",\"contenido\":\""+rows[i].contenido+"\",\"para\":\""+rows[i].para+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              if(lengthRows>0){
                socket.emit("newPublication","{\"mensaje\":\"Tienes nuevas publicaciones en la seccion de noticias FARUSAC\"}");
                socket.emit("recieverRealTimePublications",notes);
              }
            });
}


exports.authPublication=function(CodigoMaestro,socket){
  var notes;
  connection.query(`select tipo from Maestro where CodigoMaestro=?`,[CodigoMaestro], function(err, rows, fields) {
    if (!err){
      if(rows.length>0){
        notes=rows[0].tipo;
      }else{
        notes=2;
      }
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=2;
    }
  }).on('end', function(){
                socket.emit("responseAuthPublication","{\"auth\":\""+notes+"\"}");
            });
}

function notificarTodosAlumnos (notesContent,socket){
  var notes;
  connection.query(`select alumno.carne as carne, sesion.llave as keyChain from alumno
join sesion on sesion.cui=alumno.carne`, function(err, rows, fields) {
    if (!err){
      notes=rows;
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              if(notes!=0){
                console.log("VOY A NOTIFICAR PUBLICACIONES EN TIEMPO REAL A: ");
                var keys = [];
                for(i in notes){
                  //notes[i].carne (lowercase), notes[i].keyChain... realtimemessage and notification firebase
                  keys.push(notes[i].keyChain);
                }
                if(keys.length>0){
                  var parse = JSON.parse(notesContent);
                  console.log(parse);
                  var data = parse.publicacion[0];
                  sendRealTimeOneSignal(keys, "MIAPublication",data.contenido,data.contenido.substring(0, 10)+"...", "Nueva publicación FARUSAC","publication","","",data.fecha,data.para,data.idPublicacion,data.titulo);
                }
              }
            });
}



function notificarTodosMaestro (notesContent,socket){
  var notes;
  connection.query(`select Maestro.codigomaestro as codigomaestro, sesion.llave as keyChain from Maestro
join sesion on sesion.cui=maestro.codigomaestro`, function(err, rows, fields) {
    if (!err){
      notes=rows;
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              if(notes!=0){
                console.log("VOY A NOTIFICAR PUBLICACIONES EN TIEMPO REAL A: ");
                var keys = [];
                for(i in notes){
                  //notes[i].codigomaestro (lowercase), notes[i].keyChain... realtimemessage and notification firebase
                  keys.push(notes[i].keyChain);
                }
                if(keys.length>0){
                  var parse = JSON.parse(notesContent);
                  console.log(parse);
                  var data = parse.publicacion[0];
                  sendRealTimeOneSignal(keys, "MIAPublication",data.contenido,data.contenido.substring(0, 10)+"...", "Nueva publicación FARUSAC","publication","","",data.fecha,data.para,data.idPublicacion,data.titulo);
                }
              }
            });
}

exports.deleteSesion=function(username,callback){
  var notes;
  connection.query(`delete from sesion where cui=?`,[username], function(err, rows, fields) {
    if (!err){
      notes="exitoso";
    }
    else{
      console.log('Error mientras se registrarba el usuario '+username+":"+err);
      notes=err;
    }
  }).on('end', function(){
              console.log("salida on deleteSesion: "+notes);
              callback(notes);
            // console.log("estuiantes del curso seran extraidos");
            });
}


exports.registrarAlumno=function(username,password,codigo,socket){
  var notes;
  connection.query(`insert into Alumno values(?,?,?)`,[codigo,username,password], function(err, rows, fields) {
    if (!err){
      notes="exitoso";
    }
    else{
      console.log('Error mientras se registrarba el usuario '+username+":"+err);
      notes=err;
    }
  }).on('end', function(){
              console.log("salida on registrarAlumno: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"estado\":\""+notes+"\"}";
             socket.emit("recibirEstadoRegistro", cadena);
            });
}

exports.registrarSesion=function(username,keyChain,socket){
  var notes;
  connection.query(`replace into sesion values(?,?)`,[username,keyChain], function(err, rows, fields) {
    if (!err){
      notes="exitoso";
    }
    else{
      console.log('Error mientras se registrarba el usuario '+username+":"+err);
      notes=err;
    }
  }).on('end', function(){
              console.log("salida on registrarAlumno: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"estado\":\""+notes+"\"}";
             socket.emit("recibirEstadoSesion", cadena);
            });
}


exports.asignarCurso=function(username,curso,seccion,socket){
  var notes;
  connection.query(`insert into AsignacionAlumno(fkCarne,fkCodigoCurso,fkSeccion,fkSemestre,fkAnio) values(?,(select CodigoCurso from Curso where nombre=?),?,?,?)`,[username,curso,seccion,semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      notes="exitoso";
    }
    else{
      console.log('Error mientras se asignaba el usuario '+username+" al curso "+curso+":"+err);
      notes=err;
    }
  }).on('end', function(){
              console.log("salida on asignarCurso: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\",\"estado\":\""+notes+"\"}";
             socket.emit("recibirAsignacionCurso", cadena);
            });
}

exports.registrarMaestro=function(username,password,codigo,socket){
  var notes;
  connection.query(`insert into Maestro values (?,?,?,0);`,[codigo,username,password], function(err, rows, fields) {
    if (!err){
      notes="exitoso";
    }
    else{
      console.log('Error mientras se registrarba el usuario '+username+":"+err);
      notes=err;
    }
  }).on('end', function(){
              console.log("salida on registrarMaestro: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"estado\":\""+notes+"\"}";
             socket.emit("recibirEstadoRegistro", cadena);
            });
}

function listaAlumnos(curso,seccion,mensaje,socket){
  var notes;
  connection.query(`select fkCarne as Carne, fkSeccion as Seccion, Curso.Nombre as Curso, sesion.llave as keyChain
from AsignacionAlumno
join Curso on Curso.CodigoCurso=AsignacionAlumno.fkCodigoCurso
join sesion on sesion.cui = asignacionalumno.fkCarne
where AsignacionAlumno.fkSemestre=? and AsignacionAlumno.fkAnio=? and Curso.Nombre=? and AsignacionAlumno.fkSeccion=?`,[semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes=rows;
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              if(notes!=0){
                //socket.emit("enviarMensaje", notes,mensaje);
                console.log("VOY A MANDAR MENSAJE "+mensaje+" EN TIEMPO REAL A: ");
                var keys = [];
                for(i in notes){
                  //notes[i].Carne, notes[i].keyChain... here will be the magic firebase
                  keys.push(notes[i].keyChain);
                }
                if(keys.length>0){
                  sendRealTimeOneSignal(keys, curso+seccion,mensaje,curso+" "+seccion+":"+mensaje.substring(0, 10)+"...", "MIA","notification",curso,seccion,"hace pocos momentos","","","");
                }
              }
            });
}

function notificarAlumnos (username,socket){
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,AsignacionAlumno.fkSeccion as Seccion
from Instancia  
join AsignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=AsignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on AsignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where AsignacionAlumno.fkCarne=? and AsignacionAlumno.fkSemestre=? and AsignacionAlumno.fkAnio=? and Instancia.visto=0
order by Mensaje.fecha desc limit 5;`,[username,semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"visibilidad\":\"0\",\"curso\":\""+rows[i].Curso+"\",\"mensaje\":\""+rows[i].Mensaje+"\",\"catedratico\":\"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on notificar ALumnos: "+notes);
                socket.emit("inbox", notes);
            });
}
            
  exports.getAlumnos= function(curso,seccion,socket){
  var notes;
  connection.query(`select fkCarne as carne, Alumno.Nombre as nombre from asignacionalumno 
  join curso on curso.codigocurso=asignacionalumno.fkcodigocurso 
  join alumno on alumno.carne=asignacionalumno.fkCarne 
  where curso.nombre=? and fkseccion=? and fksemestre=? and fkanio=?;`,[curso,seccion,semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      notes="{\"alumnos\":[";
      for(var i in rows){
        notes+="{\"nombre\":\""+rows[i].nombre+"\",\"carne\":\""+rows[i].carne+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error while performing Query.'+err);
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on obtenerAlumnos: "+notes);
                socket.emit("recieverAlumnos", notes);
            });
}

exports.cambiarVisibilidad=function(carne,curso,seccion){
  console.log(carne+ "pide cambiar la visilidad de los mensajes del curso "+curso);
  connection.query(`UPDATE Instancia 
join AsignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=AsignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on fkCodigoCursoAlumno=Curso.CodigoCurso
SET visto=1 WHERE visto=0 and AsignacionAlumno.fkCarne=? and Curso.Nombre=? and AsignacionAlumno.fkSeccion=? and fkAnioAlumno=? and fkSemestreAlumno=?`,[carne,curso,seccion,anioActual,semestreActual], function(err, rows, fields) {
    if (!err){
      //console.log('The solution is: ', JSON.stringify(rows));
      //console.log("EXTRACCION: "+rows[0].Nombre);
      
    }
    else{
      console.log('Error al cambiar la visibilidad de los mensajes de: .'+carne+', No es un potencial error');
    }
  });
}

exports.getTopAlumno=function(carne,curso,seccion,inf,sup,socket){
  console.log(carne+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%Y-%m-%d %H:%i') As Fecha,AsignacionAlumno.fkSeccion as Seccion, Instancia.visto As Visibilidad
from Instancia  
join AsignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=AsignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on AsignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where AsignacionAlumno.fkCarne=? and AsignacionAlumno.fkSemestre=? and AsignacionAlumno.fkAnio=? and Curso.Nombre=? and AsignacionAlumno.fkSeccion=?
order by Mensaje.fecha desc limit `+inf+`,`+sup,[carne,semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"visibilidad\":\""+rows[i].Visibilidad+"\",\"curso\":\""+rows[i].Curso+"\",\"mensaje\":\""+rows[i].Mensaje+"\",\"catedratico\":\"\",\"fecha\":\""+rows[i].Fecha+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error al recibirTop de: .'+carne+', No es un potencial error');
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on getTopAlumno: "+notes);
                socket.emit("recibirTop", notes);
            });;
}


exports.getMensajesAlumno=function(carne,curso,seccion,socket){
  console.log(carne+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%Y-%m-%d %H:%i') as Fecha,AsignacionAlumno.fkSeccion as Seccion, Instancia.visto As Visibilidad
from Instancia  
join AsignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=AsignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on AsignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where AsignacionAlumno.fkCarne=? and AsignacionAlumno.fkSemestre=? and AsignacionAlumno.fkAnio=? and Curso.Nombre=? and AsignacionAlumno.fkSeccion=?
order by Mensaje.fecha desc limit 10;`,[carne,semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"visibilidad\":\""+rows[i].Visibilidad+"\",\"curso\":\""+rows[i].Curso+"\",\"mensaje\":\""+rows[i].Mensaje+"\",\"catedratico\":\"\",\"fecha\":\""+rows[i].Fecha+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error al recibirTop de: .'+carne+', No es un potencial error');
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on getMensajesAlumon: "+notes);
                socket.emit("recibirMensajes", notes);
            });;
}


exports.getMensajesMaestro=function(codigo,curso,seccion,socket){
  console.log(codigo+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,Mensaje.fkSeccion as Seccion,DATE_FORMAT(Mensaje.fecha,'%Y-%m-%d %H:%i') as Fecha
from Mensaje  
join Curso on Mensaje.fkCodigoCurso=CodigoCurso
where Mensaje.fkCodigoMaestro=? and Mensaje.fkSemestre=? and Mensaje.fkAnio=? and Curso.Nombre=? and Mensaje.fkSeccion=?
order by Mensaje.fecha desc limit 10`,[codigo,semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"visibilidad\":\"1\",\"curso\":\""+rows[i].Curso+"\",\"mensaje\":\""+rows[i].Mensaje+"\",\"catedratico\":\"\",\"fecha\":\""+rows[i].Fecha+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error al recibirTop de: .'+codigo+', No es un potencial error');
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on getMensajeMaestro: "+notes);
                socket.emit("recibirMensajes", notes);
            });;
}


exports.getTopMaestro=function(codigo,curso,seccion,inf,sup,socket){
  console.log(codigo+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%Y-%m-%d %H:%i') As Fecha,Mensaje.fkSeccion as Seccion,Mensaje.fecha
from Mensaje  
join Curso on Mensaje.fkCodigoCurso=CodigoCurso
where Mensaje.fkCodigoMaestro=? and Mensaje.fkSemestre=? and Mensaje.fkAnio=? and Curso.Nombre=? and Mensaje.fkSeccion=?
order by Mensaje.fecha desc limit `+inf+`,`+sup,[codigo,semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"visibilidad\":\"1\",\"curso\":\""+rows[i].Curso+"\",\"mensaje\":\""+rows[i].Mensaje+"\",\"fecha\":\""+rows[i].Fecha+"\",\"catedratico\":\"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error al recibirTop de: .'+codigo+', No es un potencial error');
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
              console.log("salida on getTopMaestros: "+notes);
                socket.emit("recibirTop", notes);
            });;
}



exports.getListadoCursos=function(socket){
  //console.log(carne+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre as Curso, AsignacionMaestro.fkSeccion as Seccion, Maestro.Nombre as Maestro
from AsignacionMaestro
join Curso on Curso.CodigoCurso=AsignacionMaestro.fkCodigoCurso
join Maestro on Maestro.CodigoMaestro=AsignacionMaestro.fkCodigoMaestro
where AsignacionMaestro.fkSemestre=? and AsignacionMaestro.fkAnio=?
order by Curso.Nombre,AsignacionMaestro.fkSeccion,Maestro asc`,[semestreActual,anioActual], function(err, rows, fields) {
    if (!err){
      notes="{\"arreglo\":[";
      for(var i in rows){
        notes+="{\"seccion\":\""+rows[i].Seccion+"\",\"curso\":\""+rows[i].Curso+"\",\"catedratico\":\""+rows[i].Maestro+"\"},";
      }
      notes = notes.slice(0, -1);
      notes+="]}";
    }
    else{
      console.log('Error al recibirTop de: .'+carne+', No es un potencial error');
      notes="{\"error\":\""+err+"\"}";
    }
  }).on('end', function(){
                console.log("salida listado: "+notes);
                socket.emit("recibirListadoCursos", notes);
            });;
}

exports.end =function(){
  connection.end();
  console.log("close DB");
}
  
