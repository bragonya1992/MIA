  var socket = io.connect(domainWithPort, { 'forceNew': true });
  var username='';
  var curso='';
  var seccion='';
  var datacookie = getCookieMIA();
  socket.on('connect', function(){
    console.log("actual datacookie from socket"+datacookie);
    var json = JSON.parse(datacookie);
    username = json.carne;
  	socket.emit('app_user',"{\"username\":\""+username+"\",\"role\":\"maestro\"}");
  	
    socket.emit('listaCursosMaestro',"{\"username\":\""+username+"\"}");
  });

  function getCookieMIA() {
    if(document.cookie=="" || document.cookie==null || document.cookie == undefined){
      return "{}";
    }
    var decodedCookie = decodeURIComponent(document.cookie);
    var cr = decodedCookie.split('MIAI');
    if(cr.length>1){
      var temp = cr[1];
      var cl = temp.split('MIAE');
      if(cl.length>0){
        return cl[0];
      }
    }
    return "{}";
}

  console.log("cre from cli "+domainWithPort);

  socket.on("recibirMensajes",function(msj){
  	renderMsj(msj)
  });

  socket.on("recibirCursos",function(msj){
    renderCursos(msj)
  });



  function renderMsj (data) {  

  var peticion = JSON.parse(data);
  console.log(peticion);
  var html = peticion.arreglo.slice(0).reverse().map(function(elem, index) {
    var msj =elem.mensaje;
    msj=msj.replace("$32","<br>")
        return(`       <li class="left clearfix">
                     <span class="chat-img1 pull-left">
                     <img src="img/piramide.png" alt="User Avatar" class="img-rectangular">
                     </span>
                     <div class="chat-body1 clearfix">
                        <p>${msj}</p>
                  <div class="chat_time pull-right">${elem.fecha}</div>
                     </div>
                  </li>`);
  }).join(" ");

  document.getElementById('chat_list').innerHTML = html;
  var objDiv = document.getElementById('chat_area');
objDiv.scrollTop = objDiv.scrollHeight;
}


  function renderCursos (data) {  

  var peticion = JSON.parse(data);
  console.log(peticion);
  curso=peticion.cursos[peticion.cursos.length-1].nombre;
  seccion=peticion.cursos[peticion.cursos.length-1].seccion;
  changeTitle(curso,seccion);
  socket.emit('getMensajesMaestro',"{\"username\":\""+username+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
  var html = peticion.cursos.slice(0).reverse().map(function(elem, index) {
        return(`  <li class="left clearfix">
                     <span class="chat-img pull-left">
                     <img src="img/piramide.png" alt="User Avatar" class="img-rectangular">
                     </span>
                     <div class="chat-body clearfix" onClick="changeCurse(\'${elem.nombre}\',\'${elem.seccion}\')">
                        <div class="header_sec">
                           <strong class="primary-font">${elem.nombre}</strong> 
                        </div>
                        <div class="contact_sec">
                           <strong class="primary-font">${elem.seccion}</strong> 
                        </div>
                     </div>
                  </li>`);
  }).join(" ");

  document.getElementById('curse_list').innerHTML = html;
}

function changeTitle(curse,section){
  document.getElementById('titleChat').innerHTML = curse+" "+section;
}
function changeCurse(curse, section){
  curso=curse;
  seccion=section;
  changeTitle(curso,seccion);
  socket.emit('getMensajesMaestro',"{\"username\":\""+username+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
  
}

    
function senderMsj() {
  console.log("ESTO TRAE LA CAJA"+$('#textarea').val());
  socket.emit('sendMessage',"{\"username\":\""+username+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\",\"mensaje\":\""+$('#textarea').val().replace(/\r?\n|\r/g, "$32")+"\"}");
  

  document.getElementById('chat_list').innerHTML += `       <li class="left clearfix">
                     <span class="chat-img1 pull-left">
                     <img src="img/piramide.png" alt="User Avatar" class="img-rectangular">
                     </span>
                     <div class="chat-body2 clearfix">
                        <p> `+$('#textarea').val()+` </p>
                  <div class="chat_time pull-right">Ahora</div>
                     </div>
                  </li>`;
  var objDiv = document.getElementById('chat_area');
objDiv.scrollTop = objDiv.scrollHeight;
$('#textarea').val('');
}

function closeSession(){
  document.cookie="";
  window.location.replace("/login.html");
}

function verifySession(){

    console.log("actual datacookie from verifySession"+datacookie);
    console.log("actual cookie"+document.cookie);
  if(datacookie=="{}"){
    window.location.replace("/login.html");
  }
}



$(function(){

    $('#inputQuery').keyup(function(){

        var searchText = $(this).val();

        $('#curse_list > li').each(function(){

            var currentLiText = $(this).text().toLowerCase(),
                showCurrentLi = currentLiText.indexOf(searchText.toLowerCase()) !== -1;

            $(this).toggle(showCurrentLi);

        });     
    });

});

