DELIMITER $$

DROP TRIGGER IF EXISTS `ins_transaction`$$
CREATE TRIGGER ins_transaction AFTER INSERT ON Mensaje
FOR EACH ROW 
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE Cmensaje INT;
    DECLARE Cvisto INT;
    DECLARE CcodigoCurso varchar(15);
    DECLARE Ccarne varchar(13);
    DECLARE CSeccion varchar(3);
    DECLARE CSemestre INT;
    DECLARE CAnio INT;
    DECLARE cur CURSOR FOR select Mensaje.idMensaje,0,asignacionAlumno.fkCodigoCurso,asignacionAlumno.fkCarne,asignacionAlumno.fkSeccion,asignacionAlumno.fkSemestre,asignacionAlumno.fkAnio
from asignacionAlumno
join Mensaje on Mensaje.idMensaje=new.idMensaje
where asignacionAlumno.fkCodigoCurso=new.fkCodigoCurso and asignacionAlumno.fkSeccion=new.fkSeccion and asignacionAlumno.fkSemestre=new.fkSemestre and asignacionAlumno.fkAnio=new.fkAnio;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
        ins_loop: LOOP
            FETCH cur INTO Cmensaje,Cvisto,CcodigoCurso,Ccarne,CSeccion,CSemestre,CAnio;
            IF done THEN
                LEAVE ins_loop;
            END IF;
            INSERT INTO Instancia VALUES (Cmensaje,Cvisto,CcodigoCurso,Ccarne,CSeccion,CSemestre,CAnio);
        END LOOP;
    CLOSE cur;
END$$ 
DELIMITER ;