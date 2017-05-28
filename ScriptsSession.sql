replace into sesion values("1111111111111","keyChain");


create table sesion(
cui varchar(13) primary key,
llave varchar(160)
);


select fkCarne as Carne, fkSeccion as Seccion, Curso.Nombre as Curso, sesion.llave as keyChain
from AsignacionAlumno
join Curso on Curso.CodigoCurso=AsignacionAlumno.fkCodigoCurso
join sesion on sesion.cui = asignacionalumno.fkCarne
where AsignacionAlumno.fkSemestre=2 and AsignacionAlumno.fkAnio=2016 and Curso.Nombre="Materiales" and AsignacionAlumno.fkSeccion="A";

select alumno.carne as carne, sesion.llave as keyChain from alumno
join sesion on sesion.cui=alumno.carne ;

select Maestro.codigomaestro as codigomaestro, sesion.llave as keyChain from Maestro
join sesion on sesion.cui=maestro.codigomaestro ;