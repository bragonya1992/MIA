drop database nodeprueba2;
create database nodeprueba2;
use nodeprueba2;

create table maestro(
CodigoMaestro varchar(13) primary key,
Nombre varchar(60),
Contrasena varchar(30),
Tipo int 
)ENGINE=INNODB;

create table curso(
CodigoCurso varchar(15),
Nombre varchar(60) not null unique,
primary key(CodigoCurso)
)ENGINE=INNODB;


create table alumno(
Carne varchar(13) primary key,
Nombre varchar(60),
Contrasena varchar(30)
)ENGINE=INNODB;

create table asignacionmaestro(
fkCodigoMaestro varchar(13),
fkCodigoCurso varchar(15),
fkSeccion varchar(3) not null,
fkSemestre int not null,
fkAnio int not null,
FOREIGN KEY (fkCodigoMaestro) REFERENCES maestro (CodigoMaestro) on delete cascade,
FOREIGN KEY (fkCodigoCurso) REFERENCES curso (CodigoCurso) on delete cascade,
primary key(fkCodigoMaestro,fkCodigoCurso,fkSeccion,fkSemestre,fkAnio),
fecha TIMESTAMP DEFAULT   CURRENT_TIMESTAMP
)ENGINE=INNODB;

create table asignacionalumno(
fkCarne varchar(13),
fkCodigoCurso varchar(15),
fkSeccion varchar(3),
fkSemestre int not null,
fkAnio int not null,
FOREIGN KEY (fkCarne) REFERENCES alumno(Carne) on delete cascade,
FOREIGN KEY (fkCodigoCurso) REFERENCES curso(CodigoCurso) on delete cascade,
fecha TIMESTAMP DEFAULT   CURRENT_TIMESTAMP,
primary key (fkCarne,fkCodigoCurso,fkSeccion,fkSemestre,fkAnio)
)ENGINE=INNODB;

create table mensaje(
idMensaje int primary key AUTO_INCREMENT,
fkCodigoMaestro varchar(13),
fkCodigoCurso varchar(15),
fkSeccion varchar(3),
fkSemestre int not null,
fkAnio int not null,
FOREIGN KEY (fkCodigoMaestro) REFERENCES maestro(CodigoMaestro) on delete cascade,
FOREIGN KEY (fkCodigoCurso) REFERENCES asignacionMaestro(fkCodigoCurso) on delete cascade,
fecha TIMESTAMP DEFAULT   CURRENT_TIMESTAMP,
mensaje varchar (1500) CHARACTER SET utf8mb4
)ENGINE=INNODB;

create table instancia(
fkMensaje int,
visto int not null,
fkCodigoCursoAlumno varchar(15),
fkCarne varchar(13),
fkSeccionAlumno varchar(3),
fkSemestreAlumno int not null,
fkAnioAlumno int not null,
primary key(fkMensaje,fkCodigoCursoAlumno,fkCarne,fkSeccionAlumno,fkSemestreAlumno,fkAnioAlumno),
FOREIGN KEY (fkMensaje) REFERENCES mensaje(idMensaje) on delete cascade,
FOREIGN KEY (fkCodigoCursoAlumno) REFERENCES asignacionAlumno(fkCodigoCurso) on delete cascade
)ENGINE=INNODB;

create table publicacion(
idPublicacion int AUTO_INCREMENT,
para int,
titulo varchar(50),
fkCodigoMaestro varchar(13),
fecha TIMESTAMP DEFAULT   CURRENT_TIMESTAMP,
contenido varchar (1500) CHARACTER SET utf8mb4,
primary key(idPublicacion),
FOREIGN KEY (fkCodigoMaestro) REFERENCES maestro(CodigoMaestro) on delete cascade
)ENGINE=INNODB;


create table sesion(
cui varchar(13),
llave varchar(160),
primary key (cui)
)ENGINE=INNODB;