var semestreActual=2;
var date = new Date();
var anioActual = 2016;//date.getFullYear();
var mysql      = require('mysql');
  var connection = mysql.createConnection({
    host     : 'localhost',
    user     : 'root',
    password : 'bases2',
    database : 'nodeprueba'
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


exports.autenticar=function(carne,pass,role,socket){
  var tabla ="alumno"; var campo1 ="Carne"; var rol =1;
  if(role=="Maestro"){
    var tabla ="maestro";
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
                socket.emit("responseAutenticar", notes);
            });
}


exports.getCursosMaestro=function(CodigoMaestro,socket){
  var notes;
  connection.query(`select Curso.Nombre As Nombre,Maestro.Nombre As Catedratico,asignacionMaestro.fkSeccion as Seccion from asignacionMaestro 
join Curso on fkCodigoCurso=CodigoCurso 
join Maestro on CodigoMaestro=fkCodigoMaestro 
where Maestro.CodigoMaestro=? and asignacionMaestro.fkSemestre=? and asignacionMaestro.fkAnio=?`,[CodigoMaestro,semestreActual,anioActual], function(err, rows, fields) {
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
  connection.query(`select Curso.Nombre,asignacionAlumno.fkSeccion as Seccion, (select COUNT(*) from Instancia
where fkCodigoCursoAlumno=asignacionAlumno.fkCodigoCurso and fkCarne=asignacionAlumno.fkCarne and Instancia.fkSeccionAlumno=asignacionAlumno.fkSeccion and fkSemestreAlumno=asignacionAlumno.fkSemestre and fkAnioAlumno=asignacionAlumno.fkAnio and visto=0) as Contador
from asignacionAlumno 
join Curso on fkCodigoCurso=CodigoCurso
where asignacionAlumno.fkCarne=? and fkSemestre=? and fkAnio=?
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

exports.insertMensajeMaestro=function(username,curso,seccion,mensaje,socket,app_users){
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
              console.log("salida on: "+notes);
              if(notes==1){
                // console.log("estuiantes del curso seran extraidos");
                // var cadena = "{\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\",\"mensaje\":\""+mensaje+"\"}";
                // socket.emit("estudiantesRecipientes", cadena);
                listaAlumnos(curso,seccion,mensaje,socket,app_users);
              }
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
              console.log("salida on: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"estado\":\""+notes+"\"}";
             socket.emit("recibirEstadoRegistro", cadena);
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
              console.log("salida on: "+notes);
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
              console.log("salida on: "+notes);
            // console.log("estuiantes del curso seran extraidos");
             var cadena = "{\"estado\":\""+notes+"\"}";
             socket.emit("recibirEstadoRegistro", cadena);
            });
}

function listaAlumnos(curso,seccion,mensaje,socket,app_users){
  var notes;
  connection.query(`select fkCarne as Carne, fkSeccion as Seccion, Curso.Nombre as Curso
from asignacionAlumno
join Curso on Curso.CodigoCurso=asignacionAlumno.fkCodigoCurso
where asignacionAlumno.fkSemestre=? and asignacionAlumno.fkAnio=? and Curso.Nombre=? and asignacionAlumno.fkSeccion=?`,[semestreActual,anioActual,curso,seccion], function(err, rows, fields) {
    if (!err){
      notes=rows;
    }
    else{
      console.log('Error while performing Query.'+err);
      notes=0;
    }
  }).on('end', function(){
              console.log("salida on: "+notes);
              if(notes!=0){
                //socket.emit("enviarMensaje", notes,mensaje);
                console.log("VOY A MANDAR MENSAJES EN TIEMPO REAL A: ");
                for(i in notes){
                  for(j in app_users){
                    if(notes[i].Carne==app_users[j].username){
                      console.log(i+" "+notes[i].Carne+" socket: "+j);
                      socket.connected[j].emit("recibirMensajes","{\"arreglo\":[{\"seccion\":\""+notes[i].Seccion+"\",\"visibilidad\":\""+0+"\",\"curso\":\""+notes[i].Curso+"\",\"mensaje\":\""+mensaje+"\",\"catedratico\":\"\",\"fecha\":\"Hace pocos segundos\"}]}");
                      notificacionesAlumnos(notes[i].Carne,socket.connected[j]);
                    }
                  }

                }
              }
            });
}

exports.notificarAlumnos=function (username,socket){
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,asignacionAlumno.fkSeccion as Seccion
from Instancia  
join asignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=asignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on asignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where asignacionAlumno.fkCarne=? and asignacionAlumno.fkSemestre=? and asignacionAlumno.fkAnio=? and Instancia.visto=0
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
              console.log("salida on: "+notes);
                socket.emit("inbox", notes);
            });
}

function notificacionesAlumnos(username,socket){
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,asignacionAlumno.fkSeccion as Seccion
from Instancia  
join asignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=asignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on asignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where asignacionAlumno.fkCarne=? and asignacionAlumno.fkSemestre=? and asignacionAlumno.fkAnio=? and Instancia.visto=0
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
              console.log("salida on: "+notes);
                socket.emit("inbox", notes);
            });
}

exports.cambiarVisibilidad=function(carne,curso,seccion){
  console.log(carne+ "pide cambiar la visilidad de los mensajes del curso "+curso);
  connection.query(`UPDATE Instancia 
join asignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=asignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on fkCodigoCursoAlumno=Curso.CodigoCurso
SET visto=1 WHERE visto=0 and asignacionAlumno.fkCarne=? and Curso.Nombre=? and asignacionAlumno.fkSeccion=? and fkAnioAlumno=? and fkSemestreAlumno=?`,[carne,curso,seccion,anioActual,semestreActual], function(err, rows, fields) {
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
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%b %d %Y %h:%i %p') As Fecha,asignacionAlumno.fkSeccion as Seccion, Instancia.visto As Visibilidad
from Instancia  
join asignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=asignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on asignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where asignacionAlumno.fkCarne=? and asignacionAlumno.fkSemestre=? and asignacionAlumno.fkAnio=? and Curso.Nombre=? and asignacionAlumno.fkSeccion=?
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
              console.log("salida on: "+notes);
                socket.emit("recibirTop", notes);
            });;
}


exports.getMensajesAlumno=function(carne,curso,seccion,socket){
  console.log(carne+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%b %d %Y %h:%i %p') as Fecha,asignacionAlumno.fkSeccion as Seccion, Instancia.visto As Visibilidad
from Instancia  
join asignacionAlumno on fkCodigoCursoAlumno=fkCodigoCurso and Instancia.fkCarne=asignacionAlumno.fkCarne and fkSeccionAlumno=fkSeccion and fkSemestreAlumno=fkSemestre and fkAnioAlumno=fkAnio
join Curso on asignacionAlumno.fkCodigoCurso=CodigoCurso
join Mensaje on Instancia.fkMensaje=Mensaje.idMensaje
where asignacionAlumno.fkCarne=? and asignacionAlumno.fkSemestre=? and asignacionAlumno.fkAnio=? and Curso.Nombre=? and asignacionAlumno.fkSeccion=?
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
              console.log("salida on: "+notes);
                socket.emit("recibirMensajes", notes);
            });;
}


exports.getMensajesMaestro=function(codigo,curso,seccion,socket){
  console.log(codigo+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,Mensaje.fkSeccion as Seccion,DATE_FORMAT(Mensaje.fecha,'%b %d %Y %h:%i %p') as Fecha
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
              console.log("salida on: "+notes);
                socket.emit("recibirMensajes", notes);
            });;
}


exports.getTopMaestro=function(codigo,curso,seccion,inf,sup,socket){
  console.log(codigo+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre As Curso,Mensaje.mensaje As Mensaje,DATE_FORMAT(Mensaje.fecha,'%b %d %Y %h:%i %p') As Fecha,Mensaje.fkSeccion as Seccion,Mensaje.fecha
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
              console.log("salida on: "+notes);
                socket.emit("recibirTop", notes);
            });;
}



exports.getListadoCursos=function(socket){
  //console.log(carne+ "pide mas mensajes del curso "+curso);
  var notes;
  connection.query(`select Curso.Nombre as Curso, asignacionMaestro.fkSeccion as Seccion, Maestro.Nombre as Maestro
from asignacionMaestro
join Curso on Curso.CodigoCurso=asignacionMaestro.fkCodigoCurso
join Maestro on Maestro.CodigoMaestro=asignacionMaestro.fkCodigoMaestro
where asignacionMaestro.fkSemestre=? and asignacionMaestro.fkAnio=?
order by Curso.Nombre,asignacionMaestro.fkSeccion,Maestro asc`,[semestreActual,anioActual], function(err, rows, fields) {
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
  
