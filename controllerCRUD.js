  var socket = io.connect(domainWithPort, { 'forceNew': true });


    socket.on('connect', function(){
	  });

	  socket.on("reciever",function(msj){
	    alert(msj)
	  });

function insertAsignacionMaestro(){
	if(isValid($('#txtCUIAsignacion').val()) && isValid($('#txtCodigoAsignacion').val()) && isValid($('#txtSeccionAsignacion').val())){
	  var username = $('#txtCUIAsignacion').val();
	  var curso= $('#txtCodigoAsignacion').val();
	  var seccion= $('#txtSeccionAsignacion').val();
	  socket.emit('insertAsignacionMaestro',"{\"username\":\""+username+"\",\"curso\":\""+curso+"\",\"seccion\":\""+seccion+"\"}");
	  $('#txtCUIAsignacion').val('');
	  $('#txtCodigoAsignacion').val('');
	  $('#txtSeccionAsignacion').val('');
	}else{
		alert("falta algun campo obligatorio")
	}
}

function insertCurso(){
	if(isValid($('#txtCodigoCurso').val()) && isValid($('#txtNombreCurso').val()))
	{
	  var codigo = $('#txtCodigoCurso').val();
	  var nombre= $('#txtNombreCurso').val();
	  socket.emit('insertCurso',"{\"codigo\":\""+codigo+"\",\"nombre\":\""+nombre+"\"}");
	  $('#txtCodigoCurso').val('');
	  $('#txtNombreCurso').val('');
	}else{
		alert("falta algun campo obligatorio")
	}
}

function updateSuperUser(){
	if(isValid($('#txtCUISuper').val()))
	{
	    var codigo = $('#txtCUISuper').val();
		socket.emit('updateSuperUser',"{\"codigo\":\""+codigo+"\"}");
		$('#txtCUISuper').val('');
	}else{
			alert("falta algun campo obligatorio")
	}
}

function isValid(param){
	if(param!=null && param!='undefined' && param!=''){
		return true;
	}
	return false;
}