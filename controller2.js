  var socket = io.connect('http://localhost:8081', { 'forceNew': true });
  var username='1';
  var curso='Materiales';
  var seccion='A';
  socket.on('connect', function(){
  });

  socket.on("responseAutenticar",function(msj){
    inToTheSystem(msj)
  });
$(function() {

    $('#login-form-link').click(function(e) {
    $("#login-form").delay(100).fadeIn(100);
    $("#register-form").fadeOut(100);
    $('#register-form-link').removeClass('active');
    $(this).addClass('active');
    e.preventDefault();
  });
  $('#register-form-link').click(function(e) {
    $("#register-form").delay(100).fadeIn(100);
    $("#login-form").fadeOut(100);
    $('#login-form-link').removeClass('active');
    $(this).addClass('active');
    e.preventDefault();
  });

});

function requestForLogIn(){
  console.log("se emitio la peticion");
  document.getElementById("loader").style.display = "block";
  
    socket.emit('autenticar',"{\"carne\":\""+$('#username').val()+"\",\"pass\":\""+$('#password').val()+"\",\"role\":\"Maestro\"}");
    $('#username').val('');
    $('#password').val('');
}


  function inToTheSystem(data){
    var peticion = JSON.parse(data);
    if(peticion.error){
      document.getElementById("myAlert").style.display = "block";
    }else{
      document.cookie= data;
      window.location.replace("/mensajeexpress.html");
    }
    document.getElementById("loader").style.display = "none";
  }