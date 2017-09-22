  var socket = io.connect(domainWithPort, { 'forceNew': true });
  var username='1';
  var curso='Materiales';
  var seccion='A';
  socket.on('connect', function(){
  });

console.log("cre from cli "+domainWithPort);
  socket.on("responseAutenticar",function(msj){
    inToTheSystem(msj)
  });
  
  socket.on("recibirEstadoRegistro",function(msj){
    responseRegister(msj)
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

  $(document).ready(function(){
    $("#username_login").keypress(function(event){
        var inputValue = event.which;
        // allow letters and whitespaces only.
        if(!(inputValue >= 65 && inputValue <= 120) && (inputValue != 32 && inputValue != 0)) { 
            event.preventDefault(); 
        }
    });
});


  function requestLogInSystem(){
    if($('#pass_login').val()===$('#pass2_login').val()){
      if($('#pass_login').val().length>7){
        if($('#codigo_login').val().length==13){
          document.getElementById("loader").style.display = "block";
          socket.emit('registrarUsuario',"{\"username\":\""+$('#username_login').val()+"\",\"pass\":\""+$('#pass_login').val()+"\",\"role\":\"maestro\",\"codigo\":\""+$('#codigo_login').val()+"\"}");
          $('#username_login').val('');
          $('#pass_login').val('');
          $('#pass2_login').val('');
          $('#codigo_login').val('');
        }else{
          document.getElementById("myAlert").style.display = "block";
          document.getElementById("myAlert").className = "alert alert-danger alert-dismissable";
          document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  El CUI deber tener 13 digitos`;  
        }
      }else{
        document.getElementById("myAlert").style.display = "block";
        document.getElementById("myAlert").className = "alert alert-danger alert-dismissable";
        document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  La contraseña debe ser mayor de 7 digitos`;
      }
    }else{
      document.getElementById("myAlert").style.display = "block";
      document.getElementById("myAlert").className = "alert alert-danger alert-dismissable";
      document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  La confirmación de contraseña no coincide`;
    }
  }

  function responseRegister(data){
    var peticion = JSON.parse(data);
    if(peticion.estado=="exitoso"){
      document.getElementById("myAlert").style.display = "block";
      document.getElementById("myAlert").className = "alert alert-success alert-dismissable";
      document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  Tu registro ha sido exitoso`;
    }else{
      document.getElementById("myAlert").style.display = "block";
      document.getElementById("myAlert").className = "alert alert-danger alert-dismissable";
      document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  `+peticion.estado;
    }
    document.getElementById("loader").style.display = "none";
  }

  function inToTheSystem(data){
    var peticion = JSON.parse(data);
    if(peticion.error){
      document.getElementById("myAlert").style.display = "block";
      document.getElementById("myAlert").className = "alert alert-danger alert-dismissable";
      document.getElementById("myAlert").innerHTML=`<a href="#" class="close" onclick="$('#myAlert').hide()">&times;</a>
  Tus datos no son correctos, vuelve a intentarlo`;
    }else{
      document.cookie= data;
      window.location.replace("/mensajeexpress.html");
    }
    document.getElementById("loader").style.display = "none";
  }