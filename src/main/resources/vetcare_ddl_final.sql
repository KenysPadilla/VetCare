PROMPT '======================================================';
PROMPT '  VetCare DDL Final v3.0  -  Iniciando instalación...';
PROMPT '======================================================';

BEGIN EXECUTE IMMEDIATE 'DROP PACKAGE PKG_REPORTES';    EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PACKAGE PKG_INVENTARIO';  EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PACKAGE PKG_FACTURACION'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PACKAGE PKG_CITAS';       EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_FACTURACION_PROPIETARIO'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_INVENTARIO_ESTADO';       EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_HISTORIAL_PACIENTE';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VW_CITAS_COMPLETA';          EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_stock_critico';               EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_tiene_vacunas_vencidas';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_nombre_completo_propietario'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_vet_disponible';              EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_calcular_total_con_iva';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_costo_internacion';           EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP FUNCTION  fn_edad_mascota';                EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_generar_factura_rapida';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_descontar_stock_vacuna';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_aceptar_solicitud_cita';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_cancelar_cita';               EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_dar_egreso_internacion';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP PROCEDURE sp_completar_consulta';          EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP TABLE DETALLE_FACTURA    CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE FACTURA            CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE SERVICIO_ESTETICO  CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE VACUNACION         CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE EXAMEN_LAB         CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE CIRUGIA            CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE INTERNACION        CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE CONSULTA           CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE CITA               CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE SOLICITUD_CITA     CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PACIENTE           CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE VACUNA             CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE MEDICAMENTO        CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE USUARIO            CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ESTILISTA          CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE VETERINARIO        CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE PROPIETARIO        CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_DETALLE_FACTURA';  EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_FACTURA';          EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_SERVICIO_ESTETICO';EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VACUNACION';       EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CIRUGIA';          EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_EXAMEN_LAB';       EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_INTERNACION';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CONSULTA';         EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CITA';             EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_SOLICITUD_CITA';   EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_PACIENTE';         EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_VACUNA';           EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_MEDICAMENTO';      EXCEPTION WHEN OTHERS THEN NULL; END;
/

PROMPT '[OK] Sección 1 — Limpieza previa completada.';

CREATE SEQUENCE SEQ_MEDICAMENTO      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_VACUNA           START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_PACIENTE         START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SOLICITUD_CITA   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_CITA             START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_CONSULTA         START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_INTERNACION      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_EXAMEN_LAB       START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_CIRUGIA          START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_VACUNACION       START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_SERVICIO_ESTETICO START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_FACTURA          START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_DETALLE_FACTURA  START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

PROMPT '[OK] Sección 2 — Secuencias creadas.';

CREATE TABLE PROPIETARIO (
    cedula    VARCHAR2(10)  NOT NULL,
    nombre    VARCHAR2(50)  NOT NULL,
    apellido  VARCHAR2(50)  NOT NULL,
    telefono  VARCHAR2(10),
    email     VARCHAR2(100),
    direccion VARCHAR2(100),
    CONSTRAINT PK_PROPIETARIO PRIMARY KEY (cedula)
);

CREATE TABLE VETERINARIO (
    cedula          VARCHAR2(10)  NOT NULL,
    nombre          VARCHAR2(50)  NOT NULL,
    apellido        VARCHAR2(50)  NOT NULL,
    telefono        VARCHAR2(10),
    email           VARCHAR2(100),
    especialidad    VARCHAR2(20),
    numero_licencia VARCHAR2(15),
    activo          NUMBER(1)     DEFAULT 1 NOT NULL,
    CONSTRAINT PK_VETERINARIO        PRIMARY KEY (cedula),
    CONSTRAINT CHK_VETERINARIO_ACTIVO CHECK (activo IN (0, 1))
);

CREATE TABLE ESTILISTA (
    cedula                VARCHAR2(10)  NOT NULL,
    nombre                VARCHAR2(50)  NOT NULL,
    apellido              VARCHAR2(50)  NOT NULL,
    telefono              VARCHAR2(10),
    email                 VARCHAR2(100),
    especialidad_estetica VARCHAR2(20),
    activo                NUMBER(1)     DEFAULT 1 NOT NULL,
    CONSTRAINT PK_ESTILISTA        PRIMARY KEY (cedula),
    CONSTRAINT CHK_ESTILISTA_ACTIVO CHECK (activo IN (0, 1))
);

CREATE TABLE USUARIO (
    nombre_usuario      VARCHAR2(15)  NOT NULL,
    contrasena          VARCHAR2(100) NOT NULL,
    rol                 VARCHAR2(15)  NOT NULL,
    activo              NUMBER(1)     DEFAULT 1 NOT NULL,
    codigo_recuperacion VARCHAR2(10),
    expiracion_codigo   TIMESTAMP,
    cedula_empleado     VARCHAR2(10),
    CONSTRAINT PK_USUARIO         PRIMARY KEY (nombre_usuario),
    CONSTRAINT CHK_USUARIO_ROL    CHECK (rol    IN ('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'ESTILISTA')),
    CONSTRAINT CHK_USUARIO_ACTIVO CHECK (activo IN (0, 1))
);

CREATE TABLE MEDICAMENTO (
    id                NUMBER(10)    NOT NULL,
    nombre            VARCHAR2(30)  NOT NULL,
    descripcion       VARCHAR2(500),
    fabricante        VARCHAR2(15),
    precio            NUMBER(8,2)   NOT NULL,
    stock_disponible  NUMBER(3)     DEFAULT 0 NOT NULL,
    fecha_vencimiento DATE,
    concentracion     VARCHAR2(20),
    categoria         VARCHAR2(50),
    CONSTRAINT PK_MEDICAMENTO        PRIMARY KEY (id),
    CONSTRAINT CHK_MEDICAMENTO_PRECIO CHECK (precio >= 0),
    CONSTRAINT CHK_MEDICAMENTO_STOCK  CHECK (stock_disponible >= 0)
);

CREATE TABLE VACUNA (
    id                NUMBER(10)    NOT NULL,
    nombre            VARCHAR2(20)  NOT NULL,
    laboratorio       VARCHAR2(15),
    lote              VARCHAR2(15),
    precio            NUMBER(8,2)   NOT NULL,
    stock_disponible  NUMBER(3)     DEFAULT 0 NOT NULL,
    fecha_vencimiento DATE,
    CONSTRAINT PK_VACUNA        PRIMARY KEY (id),
    CONSTRAINT CHK_VACUNA_PRECIO CHECK (precio >= 0),
    CONSTRAINT CHK_VACUNA_STOCK  CHECK (stock_disponible >= 0)
);

PROMPT '[OK] Sección 3 — Tablas base creadas.';

CREATE TABLE PACIENTE (
    id                 NUMBER(10)    NOT NULL,
    nombre             VARCHAR2(20)  NOT NULL,
    especie            VARCHAR2(15),
    raza               VARCHAR2(20),
    sexo               VARCHAR2(15),
    peso               NUMBER(6,2),
    fecha_nacimiento   DATE,
    microchip          VARCHAR2(15),
    cedula_propietario VARCHAR2(10),
    CONSTRAINT PK_PACIENTE            PRIMARY KEY (id),
    CONSTRAINT FK_PACIENTE_PROPIETARIO FOREIGN KEY (cedula_propietario)
        REFERENCES PROPIETARIO (cedula)
);

CREATE INDEX IDX_PACIENTE_PROPIETARIO ON PACIENTE (cedula_propietario);

CREATE TABLE SOLICITUD_CITA (
    id                 NUMBER(10)    NOT NULL,
    nombre_propietario VARCHAR2(50),
    telefono           VARCHAR2(10),
    correo             VARCHAR2(100),
    nombre_mascota     VARCHAR2(20),
    especie            VARCHAR2(15),
    raza               VARCHAR2(20),
    motivo             VARCHAR2(250),
    fecha              DATE,
    hora               VARCHAR2(5),
    estado             VARCHAR2(15)  DEFAULT 'PENDIENTE' NOT NULL,
    fecha_solicitud    TIMESTAMP     DEFAULT SYSTIMESTAMP,
    CONSTRAINT PK_SOLICITUD_CITA    PRIMARY KEY (id),
    CONSTRAINT CHK_SOLICITUD_ESTADO CHECK (estado IN ('PENDIENTE', 'ACEPTADA', 'RECHAZADA'))
);

CREATE INDEX IDX_SOLICITUD_FECHA           ON SOLICITUD_CITA (fecha);
CREATE INDEX IDX_SOLICITUD_FECHA_SOLICITUD ON SOLICITUD_CITA (fecha_solicitud);
CREATE INDEX IDX_SOLICITUD_ESTADO          ON SOLICITUD_CITA (estado);

CREATE TABLE CITA (
    id                 NUMBER(10)    NOT NULL,
    id_paciente        NUMBER(10)    NOT NULL,
    cedula_veterinario VARCHAR2(10)  NOT NULL,
    id_solicitud_cita  NUMBER(10),
    fecha_hora         TIMESTAMP     NOT NULL,
    tipo_cita          VARCHAR2(20),
    estado_cita        VARCHAR2(15)  DEFAULT 'PROGRAMADA' NOT NULL,
    motivo             VARCHAR2(250),
    observaciones      VARCHAR2(500),
    CONSTRAINT PK_CITA             PRIMARY KEY (id),
    CONSTRAINT FK_CITA_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_CITA_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT FK_CITA_SOLICITUD    FOREIGN KEY (id_solicitud_cita)  REFERENCES SOLICITUD_CITA (id),
    CONSTRAINT CHK_CITA_ESTADO     CHECK (estado_cita IN ('PROGRAMADA', 'EN_CURSO', 'REALIZADA', 'CANCELADA'))
);

CREATE INDEX IDX_CITA_PACIENTE    ON CITA (id_paciente);
CREATE INDEX IDX_CITA_VETERINARIO ON CITA (cedula_veterinario);
CREATE INDEX IDX_CITA_FECHA_HORA  ON CITA (fecha_hora);
CREATE INDEX IDX_CITA_ESTADO      ON CITA (estado_cita);

CREATE TABLE CONSULTA (
    id                 NUMBER(10)    NOT NULL,
    id_cita            NUMBER(10),
    id_paciente        NUMBER(10)    NOT NULL,
    cedula_veterinario VARCHAR2(10)  NOT NULL,
    fecha_hora         TIMESTAMP     NOT NULL,
    sintomas           VARCHAR2(250),
    diagnostico        VARCHAR2(250),
    tratamiento        VARCHAR2(500),
    costo              NUMBER(8,2)   DEFAULT 0,
    CONSTRAINT PK_CONSULTA             PRIMARY KEY (id),
    CONSTRAINT FK_CONSULTA_CITA        FOREIGN KEY (id_cita)            REFERENCES CITA        (id),
    CONSTRAINT FK_CONSULTA_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_CONSULTA_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT UQ_CONSULTA_CITA        UNIQUE (id_cita),
    CONSTRAINT CHK_CONSULTA_COSTO      CHECK (costo >= 0)
);

CREATE INDEX IDX_CONSULTA_PACIENTE   ON CONSULTA (id_paciente);
CREATE INDEX IDX_CONSULTA_VETERINARIO ON CONSULTA (cedula_veterinario);
CREATE INDEX IDX_CONSULTA_FECHA_HORA  ON CONSULTA (fecha_hora);

CREATE TABLE INTERNACION (
    id                   NUMBER(10)    NOT NULL,
    id_paciente          NUMBER(10)    NOT NULL,
    cedula_veterinario   VARCHAR2(10)  NOT NULL,
    id_consulta          NUMBER(10),
    fecha_hora_ingreso   TIMESTAMP     NOT NULL,
    fecha_hora_egreso    TIMESTAMP,
    motivo               VARCHAR2(500),
    diagnostico          VARCHAR2(500),
    costo_diario         NUMBER(8,2)   DEFAULT 0,
    observaciones        VARCHAR2(500),
    costo_medicamentos   NUMBER(8,2)   DEFAULT 0,
    medicamentos_detalle VARCHAR2(500),
    CONSTRAINT PK_INTERNACION            PRIMARY KEY (id),
    CONSTRAINT FK_INTERNACION_PACIENTE   FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_INTERNACION_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT FK_INTERNACION_CONSULTA   FOREIGN KEY (id_consulta)        REFERENCES CONSULTA    (id),
    CONSTRAINT CHK_INTERNACION_COSTO     CHECK (costo_diario >= 0)
);

CREATE INDEX IDX_INTERNACION_PACIENTE    ON INTERNACION (id_paciente);
CREATE INDEX IDX_INTERNACION_VETERINARIO ON INTERNACION (cedula_veterinario);
CREATE INDEX IDX_INTERNACION_CONSULTA    ON INTERNACION (id_consulta);
CREATE INDEX IDX_INTERNACION_INGRESO     ON INTERNACION (fecha_hora_ingreso);

CREATE TABLE EXAMEN_LAB (
    id                 NUMBER(10)    NOT NULL,
    id_paciente        NUMBER(10)    NOT NULL,
    cedula_veterinario VARCHAR2(10)  NOT NULL,
    id_consulta        NUMBER(10),
    fecha_hora         TIMESTAMP     NOT NULL,
    tipo_examen        VARCHAR2(50),
    prioridad          VARCHAR2(10)  DEFAULT 'NORMAL',
    resultado          VARCHAR2(250),
    observaciones      VARCHAR2(500),
    costo              NUMBER(8,2)   DEFAULT 0,
    CONSTRAINT PK_EXAMEN_LAB         PRIMARY KEY (id),
    CONSTRAINT FK_EXAMEN_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_EXAMEN_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT FK_EXAMEN_CONSULTA    FOREIGN KEY (id_consulta)        REFERENCES CONSULTA    (id),
    CONSTRAINT CHK_EXAMEN_COSTO      CHECK (costo >= 0)
);

CREATE INDEX IDX_EXAMEN_PACIENTE    ON EXAMEN_LAB (id_paciente);
CREATE INDEX IDX_EXAMEN_VETERINARIO ON EXAMEN_LAB (cedula_veterinario);
CREATE INDEX IDX_EXAMEN_CONSULTA    ON EXAMEN_LAB (id_consulta);
CREATE INDEX IDX_EXAMEN_FECHA_HORA  ON EXAMEN_LAB (fecha_hora);

CREATE TABLE CIRUGIA (
    id                 NUMBER(10)    NOT NULL,
    id_paciente        NUMBER(10)    NOT NULL,
    cedula_veterinario VARCHAR2(10)  NOT NULL,
    id_consulta        NUMBER(10),
    fecha_hora         TIMESTAMP     NOT NULL,
    tipo_cirugia       VARCHAR2(60),
    anestesia          VARCHAR2(25),
    descripcion        VARCHAR2(500),
    resultado          VARCHAR2(250),
    duracion           NUMBER(5),
    estado      VARCHAR2(20) DEFAULT 'Programada' NOT NULL,
    hora_inicio TIMESTAMP,
    hora_fin    TIMESTAMP,
    costo              NUMBER(8,2)   DEFAULT 0,
    CONSTRAINT PK_CIRUGIA             PRIMARY KEY (id),
    CONSTRAINT FK_CIRUGIA_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_CIRUGIA_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT FK_CIRUGIA_CONSULTA    FOREIGN KEY (id_consulta)        REFERENCES CONSULTA    (id),
    CONSTRAINT CHK_CIRUGIA_COSTO      CHECK (costo >= 0)
);

CREATE INDEX IDX_CIRUGIA_PACIENTE    ON CIRUGIA (id_paciente);
CREATE INDEX IDX_CIRUGIA_VETERINARIO ON CIRUGIA (cedula_veterinario);
CREATE INDEX IDX_CIRUGIA_FECHA_HORA  ON CIRUGIA (fecha_hora);

CREATE TABLE VACUNACION (
    id                 NUMBER(10)    NOT NULL,
    id_paciente        NUMBER(10)    NOT NULL,
    cedula_veterinario VARCHAR2(10)  NOT NULL,
    id_vacuna          NUMBER(10)    NOT NULL,
    fecha_hora         TIMESTAMP     NOT NULL,
    proxima_fecha      DATE,
    observaciones      VARCHAR2(500),
    estado             VARCHAR2(15)  DEFAULT 'PROGRAMADA' NOT NULL,
    CONSTRAINT PK_VACUNACION             PRIMARY KEY (id),
    CONSTRAINT FK_VACUNACION_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT FK_VACUNACION_VETERINARIO FOREIGN KEY (cedula_veterinario) REFERENCES VETERINARIO (cedula),
    CONSTRAINT FK_VACUNACION_VACUNA      FOREIGN KEY (id_vacuna)          REFERENCES VACUNA      (id),
    CONSTRAINT CHK_VACUNACION_ESTADO     CHECK (estado IN ('PROGRAMADA','PENDIENTE','APLICADA'))
);

CREATE INDEX IDX_VACUNACION_PACIENTE    ON VACUNACION (id_paciente);
CREATE INDEX IDX_VACUNACION_VETERINARIO ON VACUNACION (cedula_veterinario);
CREATE INDEX IDX_VACUNACION_VACUNA      ON VACUNACION (id_vacuna);
CREATE INDEX IDX_VACUNACION_FECHA_HORA  ON VACUNACION (fecha_hora);

CREATE TABLE SERVICIO_ESTETICO (
    id               NUMBER(10)    NOT NULL,
    tipo_servicio    VARCHAR2(10)  NOT NULL,
    id_paciente      NUMBER(10)    NOT NULL,
    cedula_estilista VARCHAR2(10)  NOT NULL,
    fecha_hora       TIMESTAMP     NOT NULL,
    precio           NUMBER(8,2)   DEFAULT 0,
    estado_servicio  VARCHAR2(15)  DEFAULT 'PROGRAMADO' NOT NULL,
    observaciones    VARCHAR2(500),

    tipo_bano        VARCHAR2(10),
    incluye_secado   NUMBER(1)     DEFAULT 0,
    incluye_perfume  NUMBER(1)     DEFAULT 0,

    estilo_corte     VARCHAR2(15),
    largo_corte      VARCHAR2(10),
    incluye_unas     NUMBER(1)     DEFAULT 0,
    incluye_limpieza NUMBER(1)     DEFAULT 0,
    CONSTRAINT PK_SERVICIO_ESTETICO       PRIMARY KEY (id),
    CONSTRAINT FK_SERVICIO_PACIENTE       FOREIGN KEY (id_paciente)      REFERENCES PACIENTE  (id),
    CONSTRAINT FK_SERVICIO_ESTILISTA      FOREIGN KEY (cedula_estilista) REFERENCES ESTILISTA (cedula),
    CONSTRAINT CHK_SERVICIO_TIPO          CHECK (tipo_servicio   IN ('BANO', 'MOTILADA')),
    CONSTRAINT CHK_SERVICIO_ESTADO        CHECK (estado_servicio IN ('PROGRAMADO', 'REALIZADO', 'CANCELADO')),
    CONSTRAINT CHK_SERVICIO_PRECIO        CHECK (precio >= 0),
    CONSTRAINT CHK_SERVICIO_TIPO_BANO     CHECK (tipo_bano    IS NULL OR tipo_bano    IN ('BASICO', 'MEDICADO', 'ANTIPULGAS', 'HIDRATANTE')),
    CONSTRAINT CHK_SERVICIO_ESTILO_CORTE  CHECK (estilo_corte IS NULL OR estilo_corte IN ('HIGIENICO', 'ESTETICO', 'RAZA', 'PERSONALIZADO')),
    CONSTRAINT CHK_SERVICIO_LARGO_CORTE   CHECK (largo_corte  IS NULL OR largo_corte  IN ('CORTO', 'MEDIANO', 'LARGO')),
    CONSTRAINT CHK_SERVICIO_INCLUYE_SECADO   CHECK (incluye_secado   IN (0, 1)),
    CONSTRAINT CHK_SERVICIO_INCLUYE_PERFUME  CHECK (incluye_perfume  IN (0, 1)),
    CONSTRAINT CHK_SERVICIO_INCLUYE_UNAS     CHECK (incluye_unas     IN (0, 1)),
    CONSTRAINT CHK_SERVICIO_INCLUYE_LIMPIEZA CHECK (incluye_limpieza IN (0, 1))
);

CREATE INDEX IDX_SERVICIO_PACIENTE   ON SERVICIO_ESTETICO (id_paciente);
CREATE INDEX IDX_SERVICIO_ESTILISTA  ON SERVICIO_ESTETICO (cedula_estilista);
CREATE INDEX IDX_SERVICIO_FECHA_HORA ON SERVICIO_ESTETICO (fecha_hora);
CREATE INDEX IDX_SERVICIO_ESTADO     ON SERVICIO_ESTETICO (estado_servicio);

CREATE TABLE FACTURA (
    id                 NUMBER(10)    NOT NULL,
    cedula_propietario VARCHAR2(10),
    id_paciente        NUMBER(10),
    fecha_hora         TIMESTAMP     NOT NULL,
    subtotal           NUMBER(9,2)   DEFAULT 0,
    total              NUMBER(9,2)   DEFAULT 0,
    estado_factura     VARCHAR2(15)  DEFAULT 'PENDIENTE',
    metodo_pago        VARCHAR2(15),
    CONSTRAINT PK_FACTURA             PRIMARY KEY (id),
    CONSTRAINT FK_FACTURA_PROPIETARIO FOREIGN KEY (cedula_propietario) REFERENCES PROPIETARIO (cedula),
    CONSTRAINT FK_FACTURA_PACIENTE    FOREIGN KEY (id_paciente)        REFERENCES PACIENTE    (id),
    CONSTRAINT CHK_FACTURA_ESTADO     CHECK (estado_factura IN ('PENDIENTE', 'PAGADA', 'ANULADA'))
);

CREATE INDEX IDX_FACTURA_PROPIETARIO ON FACTURA (cedula_propietario);
CREATE INDEX IDX_FACTURA_PACIENTE    ON FACTURA (id_paciente);
CREATE INDEX IDX_FACTURA_FECHA_HORA  ON FACTURA (fecha_hora);
CREATE INDEX IDX_FACTURA_ESTADO      ON FACTURA (estado_factura);

CREATE TABLE DETALLE_FACTURA (
    id              NUMBER(10)    NOT NULL,
    id_factura      NUMBER(10),
    descripcion     VARCHAR2(250),
    tipo_concepto   VARCHAR2(15),
    cantidad        NUMBER(2)     DEFAULT 1 NOT NULL,
    precio_unitario NUMBER(8,2)   DEFAULT 0,
    subtotal        NUMBER(9,2)   DEFAULT 0,
    id_medicamento  NUMBER(10),
    CONSTRAINT PK_DETALLE_FACTURA     PRIMARY KEY (id),
    CONSTRAINT FK_DETALLE_FACTURA     FOREIGN KEY (id_factura)     REFERENCES FACTURA     (id) ON DELETE CASCADE,
    CONSTRAINT FK_DETALLE_MEDICAMENTO FOREIGN KEY (id_medicamento) REFERENCES MEDICAMENTO (id),
    CONSTRAINT CHK_DETALLE_CANTIDAD   CHECK (cantidad > 0),
    CONSTRAINT CHK_DETALLE_PRECIO     CHECK (precio_unitario >= 0),
    CONSTRAINT CHK_DETALLE_SUBTOTAL   CHECK (subtotal >= 0)
);

CREATE INDEX IDX_DETALLE_FACTURA ON DETALLE_FACTURA (id_factura);

PROMPT '[OK] Sección 4 — Tablas con dependencias creadas.';

CREATE OR REPLACE TRIGGER TRG_MEDICAMENTO_BI
    BEFORE INSERT ON MEDICAMENTO FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_MEDICAMENTO.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_VACUNA_BI
    BEFORE INSERT ON VACUNA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_VACUNA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_PACIENTE_BI
    BEFORE INSERT ON PACIENTE FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_PACIENTE.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_SOLICITUD_CITA_BI
    BEFORE INSERT ON SOLICITUD_CITA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_SOLICITUD_CITA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_CITA_BI
    BEFORE INSERT ON CITA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_CITA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_CONSULTA_BI
    BEFORE INSERT ON CONSULTA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_CONSULTA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_INTERNACION_BI
    BEFORE INSERT ON INTERNACION FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_INTERNACION.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_EXAMEN_LAB_BI
    BEFORE INSERT ON EXAMEN_LAB FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_EXAMEN_LAB.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_CIRUGIA_BI
    BEFORE INSERT ON CIRUGIA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_CIRUGIA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_VACUNACION_BI
    BEFORE INSERT ON VACUNACION FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_VACUNACION.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_SERVICIO_ESTETICO_BI
    BEFORE INSERT ON SERVICIO_ESTETICO FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_SERVICIO_ESTETICO.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_FACTURA_BI
    BEFORE INSERT ON FACTURA FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN :NEW.id := SEQ_FACTURA.NEXTVAL; END;
/

CREATE OR REPLACE TRIGGER TRG_DETALLE_FACTURA_BIU
    BEFORE INSERT OR UPDATE ON DETALLE_FACTURA FOR EACH ROW
BEGIN
    IF INSERTING AND :NEW.id IS NULL THEN
        :NEW.id := SEQ_DETALLE_FACTURA.NEXTVAL;
    END IF;
    :NEW.subtotal := ROUND(:NEW.cantidad * :NEW.precio_unitario, 2);
END;
/

PROMPT '[OK] Sección 5 — Triggers de auto-ID creados (13 triggers).';

CREATE OR REPLACE FUNCTION fn_edad_mascota(p_id_paciente IN NUMBER)
    RETURN NUMBER IS
    v_fecha DATE;
BEGIN
    SELECT fecha_nacimiento INTO v_fecha FROM PACIENTE WHERE id = p_id_paciente;
    IF v_fecha IS NULL THEN RETURN NULL; END IF;
    RETURN TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE), TRUNC(v_fecha)) / 12);
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
END fn_edad_mascota;
/

CREATE OR REPLACE FUNCTION fn_costo_internacion(p_id_internacion IN NUMBER)
    RETURN NUMBER IS
    v_ingreso   TIMESTAMP;
    v_egreso    TIMESTAMP;
    v_costo_dia NUMBER(8,2);
    v_dias      NUMBER;
BEGIN
    SELECT fecha_hora_ingreso, fecha_hora_egreso, costo_diario
      INTO v_ingreso, v_egreso, v_costo_dia
      FROM INTERNACION WHERE id = p_id_internacion;
    IF v_egreso IS NULL THEN v_egreso := SYSTIMESTAMP; END IF;
    v_dias := TRUNC(CAST(v_egreso AS DATE)) - TRUNC(CAST(v_ingreso AS DATE));
    IF v_dias < 1 THEN v_dias := 1; END IF;
    RETURN ROUND(v_dias * NVL(v_costo_dia, 0), 2);
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 0;
END fn_costo_internacion;
/

CREATE OR REPLACE FUNCTION fn_calcular_total_con_iva(p_subtotal IN NUMBER)
    RETURN NUMBER IS
BEGIN
    RETURN ROUND(NVL(p_subtotal, 0) * 1.19, 2);
END fn_calcular_total_con_iva;
/

CREATE OR REPLACE FUNCTION fn_vet_disponible(
    p_cedula_vet IN VARCHAR2,
    p_fecha_hora IN TIMESTAMP
) RETURN NUMBER IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
      FROM CITA
     WHERE cedula_veterinario = p_cedula_vet
       AND estado_cita        = 'PROGRAMADA'
       AND TRUNC(CAST(fecha_hora   AS DATE), 'HH24') =
           TRUNC(CAST(p_fecha_hora AS DATE), 'HH24');
    RETURN CASE WHEN v_count = 0 THEN 1 ELSE 0 END;
END fn_vet_disponible;
/

CREATE OR REPLACE FUNCTION fn_nombre_completo_propietario(p_cedula IN VARCHAR2)
    RETURN VARCHAR2 IS
    v_nombre VARCHAR2(105);
BEGIN
    SELECT nombre || ' ' || apellido INTO v_nombre
      FROM PROPIETARIO WHERE cedula = p_cedula;
    RETURN v_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
END fn_nombre_completo_propietario;
/

CREATE OR REPLACE FUNCTION fn_tiene_vacunas_vencidas(p_id_paciente IN NUMBER)
    RETURN NUMBER IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
      FROM VACUNACION
     WHERE id_paciente  = p_id_paciente
       AND proxima_fecha IS NOT NULL
       AND proxima_fecha < TRUNC(SYSDATE);
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END fn_tiene_vacunas_vencidas;
/

CREATE OR REPLACE FUNCTION fn_stock_critico(
    p_tipo   IN VARCHAR2,
    p_id     IN NUMBER,
    p_umbral IN NUMBER DEFAULT 5
) RETURN NUMBER IS
    v_stock NUMBER;
BEGIN
    IF p_tipo = 'VACUNA' THEN
        SELECT stock_disponible INTO v_stock FROM VACUNA      WHERE id = p_id;
    ELSE
        SELECT stock_disponible INTO v_stock FROM MEDICAMENTO WHERE id = p_id;
    END IF;
    RETURN CASE WHEN v_stock <= p_umbral THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 0;
END fn_stock_critico;
/

PROMPT '[OK] Sección 6 — 7 funciones independientes creadas.';

CREATE OR REPLACE PROCEDURE sp_completar_consulta(
    p_id_cita      IN  NUMBER,
    p_id_paciente  IN  NUMBER,
    p_cedula_vet   IN  VARCHAR2,
    p_fecha_hora   IN  TIMESTAMP,
    p_sintomas     IN  VARCHAR2,
    p_diagnostico  IN  VARCHAR2,
    p_tratamiento  IN  VARCHAR2,
    p_costo        IN  NUMBER,
    p_id_consulta  OUT NUMBER
) IS
BEGIN
    INSERT INTO CONSULTA (id_cita, id_paciente, cedula_veterinario,
                          fecha_hora, sintomas, diagnostico, tratamiento, costo)
    VALUES (p_id_cita, p_id_paciente, p_cedula_vet,
            p_fecha_hora, p_sintomas, p_diagnostico, p_tratamiento, NVL(p_costo, 0))
    RETURNING id INTO p_id_consulta;

    UPDATE CITA
       SET estado_cita = 'REALIZADA'
     WHERE id          = p_id_cita
       AND estado_cita IN ('PROGRAMADA', 'EN_CURSO');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_completar_consulta;
/

CREATE OR REPLACE PROCEDURE sp_dar_egreso_internacion(
    p_id_internacion IN NUMBER,
    p_fecha_egreso   IN TIMESTAMP
) IS
    v_existe NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_existe
      FROM INTERNACION
     WHERE id = p_id_internacion AND fecha_hora_egreso IS NULL;

    IF v_existe = 0 THEN
        RAISE_APPLICATION_ERROR(-20001,
            'La internación ' || p_id_internacion || ' no existe o ya tiene egreso registrado.');
    END IF;

    UPDATE INTERNACION SET fecha_hora_egreso = p_fecha_egreso WHERE id = p_id_internacion;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_dar_egreso_internacion;
/

CREATE OR REPLACE PROCEDURE sp_cancelar_cita(p_id_cita IN NUMBER) IS
    v_estado VARCHAR2(15);
BEGIN
    SELECT estado_cita INTO v_estado FROM CITA WHERE id = p_id_cita;

    IF v_estado != 'PROGRAMADA' THEN
        RAISE_APPLICATION_ERROR(-20002,
            'Solo se pueden cancelar citas PROGRAMADAS. Estado actual: ' || v_estado);
    END IF;

    UPDATE CITA SET estado_cita = 'CANCELADA' WHERE id = p_id_cita;
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20003, 'No existe la cita con id ' || p_id_cita);
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_cancelar_cita;
/

CREATE OR REPLACE PROCEDURE sp_aceptar_solicitud_cita(
    p_id_solicitud  IN  NUMBER,
    p_cedula_vet    IN  VARCHAR2,
    p_id_cita_nueva OUT NUMBER
) IS
    v_sol           SOLICITUD_CITA%ROWTYPE;
    v_cedula_prop   VARCHAR2(10);
    v_existe_prop   NUMBER;
    v_id_paciente   NUMBER;
    v_fecha_hora    TIMESTAMP;
    v_nombre_prop   VARCHAR2(50);
    v_apellido_prop VARCHAR2(50);
    v_espacio       NUMBER;
BEGIN
    SELECT * INTO v_sol FROM SOLICITUD_CITA
     WHERE id = p_id_solicitud AND estado = 'PENDIENTE';

    v_cedula_prop := REGEXP_REPLACE(v_sol.telefono, '[^0-9]', '');

    v_espacio := INSTR(v_sol.nombre_propietario, ' ');
    IF v_espacio > 0 THEN
        v_nombre_prop   := SUBSTR(v_sol.nombre_propietario, 1, v_espacio - 1);
        v_apellido_prop := TRIM(SUBSTR(v_sol.nombre_propietario, v_espacio + 1));
    ELSE
        v_nombre_prop   := v_sol.nombre_propietario;
        v_apellido_prop := '-';
    END IF;
    IF v_apellido_prop IS NULL OR TRIM(v_apellido_prop) = '' THEN
        v_apellido_prop := '-';
    END IF;

    SELECT COUNT(*) INTO v_existe_prop FROM PROPIETARIO WHERE cedula = v_cedula_prop;
    IF v_existe_prop = 0 THEN
        INSERT INTO PROPIETARIO (cedula, nombre, apellido, telefono, email, direccion)
        VALUES (v_cedula_prop, v_nombre_prop, v_apellido_prop,
                v_sol.telefono, v_sol.correo, 'Valledupar');
    END IF;

    INSERT INTO PACIENTE (nombre, especie, raza, sexo, peso, cedula_propietario)
    VALUES (v_sol.nombre_mascota, v_sol.especie, 'Mestizo', 'Desconocido', 0, v_cedula_prop)
    RETURNING id INTO v_id_paciente;

    v_fecha_hora := TO_TIMESTAMP(
        TO_CHAR(v_sol.fecha, 'YYYY-MM-DD') || ' ' || v_sol.hora, 'YYYY-MM-DD HH24:MI');

    INSERT INTO CITA (id_paciente, cedula_veterinario, id_solicitud_cita,
                      fecha_hora, tipo_cita, estado_cita, motivo, observaciones)
    VALUES (v_id_paciente, p_cedula_vet, p_id_solicitud,
            v_fecha_hora, 'CONSULTA_GENERAL', 'PROGRAMADA',
            v_sol.motivo, 'Solicitud via chatbot VetBot')
    RETURNING id INTO p_id_cita_nueva;

    UPDATE SOLICITUD_CITA SET estado = 'ACEPTADA' WHERE id = p_id_solicitud;
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20004, 'No existe solicitud pendiente con id ' || p_id_solicitud);
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_aceptar_solicitud_cita;
/

CREATE OR REPLACE PROCEDURE sp_descontar_stock_vacuna(
    p_id_vacuna IN NUMBER,
    p_cantidad  IN NUMBER DEFAULT 1
) IS
    v_stock NUMBER;
BEGIN
    SELECT stock_disponible INTO v_stock FROM VACUNA WHERE id = p_id_vacuna FOR UPDATE;

    IF v_stock < p_cantidad THEN
        RAISE_APPLICATION_ERROR(-20005,
            'Stock insuficiente para vacuna ' || p_id_vacuna ||
            '. Disponible: ' || v_stock || ', solicitado: ' || p_cantidad);
    END IF;

    UPDATE VACUNA SET stock_disponible = stock_disponible - p_cantidad WHERE id = p_id_vacuna;
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20006, 'No existe la vacuna con id ' || p_id_vacuna);
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_descontar_stock_vacuna;
/

CREATE OR REPLACE PROCEDURE sp_generar_factura_rapida(
    p_cedula_prop   IN  VARCHAR2,
    p_id_paciente   IN  NUMBER,
    p_descripcion   IN  VARCHAR2,
    p_tipo_concepto IN  VARCHAR2,
    p_precio        IN  NUMBER,
    p_metodo_pago   IN  VARCHAR2,
    p_id_factura    OUT NUMBER
) IS
    v_subtotal NUMBER(9,2);
    v_total    NUMBER(9,2);
BEGIN
    v_subtotal := ROUND(NVL(p_precio, 0), 2);
    v_total    := fn_calcular_total_con_iva(v_subtotal);

    INSERT INTO FACTURA (cedula_propietario, id_paciente, fecha_hora,
                         subtotal, total, estado_factura, metodo_pago)
    VALUES (p_cedula_prop, p_id_paciente, SYSTIMESTAMP,
            v_subtotal, v_total, 'PENDIENTE', p_metodo_pago)
    RETURNING id INTO p_id_factura;

    INSERT INTO DETALLE_FACTURA (id_factura, descripcion, tipo_concepto,
                                 cantidad, precio_unitario, subtotal)
    VALUES (p_id_factura, p_descripcion, p_tipo_concepto, 1, v_subtotal, v_subtotal);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END sp_generar_factura_rapida;
/

PROMPT '[OK] Sección 7 — 6 procedimientos independientes creados.';

CREATE OR REPLACE TRIGGER TRG_CITA_ESTADO
    BEFORE UPDATE OF estado_cita ON CITA
    FOR EACH ROW
DECLARE
    v_ok BOOLEAN := FALSE;
BEGIN
    IF    :OLD.estado_cita = :NEW.estado_cita                                              THEN v_ok := TRUE;
    ELSIF :OLD.estado_cita = 'PROGRAMADA' AND :NEW.estado_cita IN ('EN_CURSO','CANCELADA') THEN v_ok := TRUE;
    ELSIF :OLD.estado_cita = 'EN_CURSO'   AND :NEW.estado_cita = 'REALIZADA'               THEN v_ok := TRUE;
    END IF;

    IF NOT v_ok THEN
        RAISE_APPLICATION_ERROR(-20200,
            'Transición inválida en CITA #' || :OLD.id ||
            ': ' || :OLD.estado_cita || ' -> ' || :NEW.estado_cita ||
            '. Permitidas: PROGRAMADA->EN_CURSO/CANCELADA, EN_CURSO->REALIZADA.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CONSULTA_SYNC_CITA
    AFTER INSERT ON CONSULTA
    FOR EACH ROW
DECLARE
    v_estado CITA.estado_cita%TYPE;
BEGIN
    IF :NEW.id_cita IS NOT NULL THEN
        SELECT estado_cita INTO v_estado FROM CITA WHERE id = :NEW.id_cita;

        IF v_estado = 'CANCELADA' THEN
            RAISE_APPLICATION_ERROR(-20201,
                'La cita #' || :NEW.id_cita || ' está CANCELADA; no puede tener consulta.');
        ELSIF v_estado = 'PROGRAMADA' THEN
            UPDATE CITA SET estado_cita = 'EN_CURSO' WHERE id = :NEW.id_cita;
        END IF;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN NULL;
END;
/

CREATE OR REPLACE TRIGGER TRG_CIRUGIA_VALIDA_CONSULTA
    BEFORE INSERT ON CIRUGIA
    FOR EACH ROW
DECLARE
    v_pac NUMBER(10);
BEGIN
    IF :NEW.id_consulta IS NOT NULL THEN
        SELECT id_paciente INTO v_pac FROM CONSULTA WHERE id = :NEW.id_consulta;

        IF v_pac <> :NEW.id_paciente THEN
            RAISE_APPLICATION_ERROR(-20202,
                'El paciente de la cirugía (ID=' || :NEW.id_paciente ||
                ') no coincide con el de la consulta origen (ID=' || v_pac || ').');
        END IF;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20203,
            'La consulta origen ID=' || :NEW.id_consulta || ' no existe.');
END;
/

CREATE OR REPLACE TRIGGER TRG_VACUNACION_STOCK
    BEFORE INSERT OR DELETE ON VACUNACION
    FOR EACH ROW
DECLARE
    v_stock  VACUNA.stock_disponible%TYPE;
    v_vence  VACUNA.fecha_vencimiento%TYPE;
    v_nombre VACUNA.nombre%TYPE;
    v_id     NUMBER(10);
BEGIN
    v_id := CASE WHEN INSERTING THEN :NEW.id_vacuna ELSE :OLD.id_vacuna END;

    SELECT stock_disponible, fecha_vencimiento, nombre
      INTO v_stock, v_vence, v_nombre
      FROM VACUNA WHERE id = v_id;

    IF INSERTING THEN
        IF v_vence IS NOT NULL AND v_vence < TRUNC(SYSDATE) THEN
            RAISE_APPLICATION_ERROR(-20204,
                'La vacuna "' || v_nombre || '" está vencida (' ||
                TO_CHAR(v_vence, 'DD/MM/YYYY') || '). No se puede aplicar.');
        END IF;
        IF v_stock <= 0 THEN
            RAISE_APPLICATION_ERROR(-20205,
                'Sin stock disponible para la vacuna "' || v_nombre || '".');
        END IF;
        UPDATE VACUNA SET stock_disponible = stock_disponible - 1 WHERE id = v_id;
    ELSE
        UPDATE VACUNA SET stock_disponible = stock_disponible + 1 WHERE id = v_id;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_DETALLE_ACTUALIZA_FACTURA
    AFTER INSERT OR UPDATE OR DELETE ON DETALLE_FACTURA
    FOR EACH ROW
DECLARE
    v_fac   NUMBER(10);
    v_delta NUMBER(9,2)  := 0;
BEGIN
    IF    INSERTING THEN v_fac := :NEW.id_factura; v_delta :=  :NEW.subtotal;
    ELSIF UPDATING  THEN v_fac := :NEW.id_factura; v_delta :=  :NEW.subtotal - NVL(:OLD.subtotal, 0);
    ELSIF DELETING  THEN v_fac := :OLD.id_factura; v_delta := -:OLD.subtotal;
    END IF;

    UPDATE FACTURA
       SET subtotal = ROUND(subtotal + v_delta, 2),
           total    = ROUND((subtotal + v_delta) * 1.19, 2)
     WHERE id = v_fac;
END;
/   

CREATE OR REPLACE TRIGGER TRG_DETALLE_STOCK_MEDICAMENTO
    BEFORE INSERT OR UPDATE OR DELETE ON DETALLE_FACTURA
    FOR EACH ROW
DECLARE
    v_stock  MEDICAMENTO.stock_disponible%TYPE;
    v_vence  MEDICAMENTO.fecha_vencimiento%TYPE;
    v_nombre MEDICAMENTO.nombre%TYPE;
    v_cn     NUMBER;
    v_co     NUMBER;
BEGIN
    IF INSERTING AND :NEW.tipo_concepto = 'MEDICAMENTO' AND :NEW.id_medicamento IS NOT NULL THEN
        SELECT stock_disponible, fecha_vencimiento, nombre
          INTO v_stock, v_vence, v_nombre
          FROM MEDICAMENTO WHERE id = :NEW.id_medicamento;
        IF v_vence IS NOT NULL AND v_vence < TRUNC(SYSDATE) THEN
            RAISE_APPLICATION_ERROR(-20206,
                'El medicamento "' || v_nombre || '" está vencido. No se puede facturar.');
        END IF;
        v_cn := ROUND(:NEW.cantidad);
        IF v_stock < v_cn THEN
            RAISE_APPLICATION_ERROR(-20207,
                'Stock insuficiente de "' || v_nombre ||
                '". Disponible: ' || v_stock || ', solicitado: ' || v_cn || '.');
        END IF;
        UPDATE MEDICAMENTO SET stock_disponible = stock_disponible - v_cn
         WHERE id = :NEW.id_medicamento;

    ELSIF UPDATING AND :NEW.tipo_concepto = 'MEDICAMENTO' AND :NEW.id_medicamento IS NOT NULL THEN
        v_cn := ROUND(:NEW.cantidad);
        v_co := ROUND(NVL(:OLD.cantidad, 0));
        IF :OLD.id_medicamento IS NOT NULL THEN
            UPDATE MEDICAMENTO SET stock_disponible = stock_disponible + v_co
             WHERE id = :OLD.id_medicamento;
        END IF;
        SELECT stock_disponible, nombre INTO v_stock, v_nombre
          FROM MEDICAMENTO WHERE id = :NEW.id_medicamento;
        IF v_stock < v_cn THEN
            IF :OLD.id_medicamento IS NOT NULL THEN
                UPDATE MEDICAMENTO SET stock_disponible = stock_disponible - v_co
                 WHERE id = :OLD.id_medicamento;
            END IF;
            RAISE_APPLICATION_ERROR(-20208,
                'Stock insuficiente al actualizar "' || v_nombre || '".');
        END IF;
        UPDATE MEDICAMENTO SET stock_disponible = stock_disponible - v_cn
         WHERE id = :NEW.id_medicamento;

    ELSIF DELETING AND :OLD.tipo_concepto = 'MEDICAMENTO' AND :OLD.id_medicamento IS NOT NULL THEN
        UPDATE MEDICAMENTO SET stock_disponible = stock_disponible + ROUND(:OLD.cantidad)
         WHERE id = :OLD.id_medicamento;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_SOLICITUD_ESTADO
    BEFORE UPDATE OF estado ON SOLICITUD_CITA
    FOR EACH ROW
BEGIN
    IF :OLD.estado IN ('ACEPTADA', 'RECHAZADA') AND :NEW.estado = 'PENDIENTE' THEN
        RAISE_APPLICATION_ERROR(-20209,
            'No se puede revertir la solicitud #' || :OLD.id ||
            ' (' || :OLD.estado || ') a PENDIENTE.');
    END IF;
END;
/

PROMPT '[OK] Sección 8 — 7 triggers de negocio creados.';

CREATE OR REPLACE PACKAGE PKG_CITAS AS

    PROCEDURE crear_cita(
        p_id_paciente IN  NUMBER,   p_cedula_vet  IN  VARCHAR2,
        p_fecha_hora  IN  TIMESTAMP, p_tipo_cita  IN  VARCHAR2,
        p_motivo      IN  VARCHAR2 DEFAULT NULL,
        p_id_cita     OUT NUMBER
    );
    PROCEDURE cancelar_cita(p_id_cita IN NUMBER, p_motivo IN VARCHAR2 DEFAULT NULL);
    PROCEDURE aceptar_solicitud(p_id_solicitud IN NUMBER, p_cedula_vet IN VARCHAR2, p_id_cita OUT NUMBER);
    PROCEDURE completar_consulta(
        p_id_cita     IN  NUMBER,   p_id_paciente IN  NUMBER,   p_cedula_vet  IN  VARCHAR2,
        p_fecha_hora  IN  TIMESTAMP DEFAULT SYSTIMESTAMP,
        p_sintomas    IN  VARCHAR2 DEFAULT NULL, p_diagnostico IN VARCHAR2 DEFAULT NULL,
        p_tratamiento IN  VARCHAR2 DEFAULT NULL, p_costo       IN NUMBER   DEFAULT 0,
        p_id_consulta OUT NUMBER
    );
    PROCEDURE consulta_sin_cita(
        p_id_paciente IN  NUMBER,   p_cedula_vet  IN  VARCHAR2,
        p_sintomas    IN  VARCHAR2 DEFAULT NULL, p_diagnostico IN VARCHAR2 DEFAULT NULL,
        p_tratamiento IN  VARCHAR2 DEFAULT NULL, p_costo       IN NUMBER   DEFAULT 0,
        p_id_consulta OUT NUMBER
    );
    FUNCTION citas_vet_en_fecha(p_cedula_vet IN VARCHAR2, p_fecha IN DATE) RETURN NUMBER;
    FUNCTION vet_disponible(p_cedula_vet IN VARCHAR2, p_fecha_hora IN TIMESTAMP) RETURN BOOLEAN;

END PKG_CITAS;
/

CREATE OR REPLACE PACKAGE BODY PKG_CITAS AS

    PROCEDURE crear_cita(
        p_id_paciente IN NUMBER, p_cedula_vet IN VARCHAR2,
        p_fecha_hora IN TIMESTAMP, p_tipo_cita IN VARCHAR2,
        p_motivo IN VARCHAR2 DEFAULT NULL, p_id_cita OUT NUMBER
    ) IS
    BEGIN
        IF fn_vet_disponible(p_cedula_vet, p_fecha_hora) = 0 THEN
            RAISE_APPLICATION_ERROR(-20300, 'El veterinario no tiene disponibilidad en esa franja.');
        END IF;
        INSERT INTO CITA (id_paciente, cedula_veterinario, fecha_hora, tipo_cita, motivo)
        VALUES (p_id_paciente, p_cedula_vet, p_fecha_hora, p_tipo_cita, p_motivo)
        RETURNING id INTO p_id_cita;
    END;

    PROCEDURE cancelar_cita(p_id_cita IN NUMBER, p_motivo IN VARCHAR2 DEFAULT NULL) IS
    BEGIN
        sp_cancelar_cita(p_id_cita);
        IF p_motivo IS NOT NULL THEN
            UPDATE CITA SET observaciones = p_motivo WHERE id = p_id_cita;
        END IF;
    END;

    PROCEDURE aceptar_solicitud(p_id_solicitud IN NUMBER, p_cedula_vet IN VARCHAR2, p_id_cita OUT NUMBER) IS
    BEGIN
        sp_aceptar_solicitud_cita(p_id_solicitud, p_cedula_vet, p_id_cita);
    END;

    PROCEDURE completar_consulta(
        p_id_cita IN NUMBER, p_id_paciente IN NUMBER, p_cedula_vet IN VARCHAR2,
        p_fecha_hora IN TIMESTAMP DEFAULT SYSTIMESTAMP,
        p_sintomas IN VARCHAR2 DEFAULT NULL, p_diagnostico IN VARCHAR2 DEFAULT NULL,
        p_tratamiento IN VARCHAR2 DEFAULT NULL, p_costo IN NUMBER DEFAULT 0,
        p_id_consulta OUT NUMBER
    ) IS
    BEGIN
        sp_completar_consulta(p_id_cita, p_id_paciente, p_cedula_vet, p_fecha_hora,
                              p_sintomas, p_diagnostico, p_tratamiento, p_costo, p_id_consulta);
    END;

    PROCEDURE consulta_sin_cita(
        p_id_paciente IN NUMBER, p_cedula_vet IN VARCHAR2,
        p_sintomas IN VARCHAR2 DEFAULT NULL, p_diagnostico IN VARCHAR2 DEFAULT NULL,
        p_tratamiento IN VARCHAR2 DEFAULT NULL, p_costo IN NUMBER DEFAULT 0,
        p_id_consulta OUT NUMBER
    ) IS
    BEGIN
        INSERT INTO CONSULTA (id_cita, id_paciente, cedula_veterinario,
                              fecha_hora, sintomas, diagnostico, tratamiento, costo)
        VALUES (NULL, p_id_paciente, p_cedula_vet, SYSTIMESTAMP,
                p_sintomas, p_diagnostico, p_tratamiento, NVL(p_costo, 0))
        RETURNING id INTO p_id_consulta;
        COMMIT;
    EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE;
    END;

    FUNCTION citas_vet_en_fecha(p_cedula_vet IN VARCHAR2, p_fecha IN DATE) RETURN NUMBER IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count FROM CITA
         WHERE cedula_veterinario = p_cedula_vet
           AND TRUNC(CAST(fecha_hora AS DATE)) = TRUNC(p_fecha)
           AND estado_cita NOT IN ('CANCELADA', 'REALIZADA');
        RETURN v_count;
    END;

    FUNCTION vet_disponible(p_cedula_vet IN VARCHAR2, p_fecha_hora IN TIMESTAMP) RETURN BOOLEAN IS
    BEGIN
        RETURN fn_vet_disponible(p_cedula_vet, p_fecha_hora) = 1;
    END;

END PKG_CITAS;
/

PROMPT '[OK] PKG_CITAS creado.';

CREATE OR REPLACE PACKAGE PKG_FACTURACION AS

    TASA_IVA CONSTANT NUMBER := 0.19;

    PROCEDURE crear_factura(
        p_cedula_prop IN VARCHAR2, p_id_paciente IN NUMBER,
        p_metodo_pago IN VARCHAR2 DEFAULT NULL, p_id_factura OUT NUMBER
    );
    PROCEDURE agregar_detalle(
        p_id_factura IN NUMBER, p_tipo_concepto IN VARCHAR2, p_descripcion IN VARCHAR2,
        p_cantidad IN NUMBER, p_precio_unitario IN NUMBER,
        p_id_medicamento IN NUMBER DEFAULT NULL, p_id_detalle OUT NUMBER
    );
    PROCEDURE pagar_factura(p_id_factura IN NUMBER, p_metodo_pago IN VARCHAR2);
    PROCEDURE anular_factura(p_id_factura IN NUMBER);
    PROCEDURE factura_rapida(
        p_cedula_prop IN VARCHAR2, p_id_paciente IN NUMBER,
        p_tipo_concepto IN VARCHAR2, p_descripcion IN VARCHAR2,
        p_precio IN NUMBER, p_metodo_pago IN VARCHAR2,
        p_id_medicamento IN NUMBER DEFAULT NULL, p_id_factura OUT NUMBER
    );
    FUNCTION ingresos_periodo(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER;

END PKG_FACTURACION;
/

CREATE OR REPLACE PACKAGE BODY PKG_FACTURACION AS

    PROCEDURE crear_factura(
        p_cedula_prop IN VARCHAR2, p_id_paciente IN NUMBER,
        p_metodo_pago IN VARCHAR2 DEFAULT NULL, p_id_factura OUT NUMBER
    ) IS
    BEGIN
        INSERT INTO FACTURA (cedula_propietario, id_paciente, fecha_hora,
                             subtotal, total, estado_factura, metodo_pago)
        VALUES (p_cedula_prop, p_id_paciente, SYSTIMESTAMP, 0, 0, 'PENDIENTE', p_metodo_pago)
        RETURNING id INTO p_id_factura;
    END;

    PROCEDURE agregar_detalle(
        p_id_factura IN NUMBER, p_tipo_concepto IN VARCHAR2, p_descripcion IN VARCHAR2,
        p_cantidad IN NUMBER, p_precio_unitario IN NUMBER,
        p_id_medicamento IN NUMBER DEFAULT NULL, p_id_detalle OUT NUMBER
    ) IS
        v_estado FACTURA.estado_factura%TYPE;
    BEGIN
        SELECT estado_factura INTO v_estado FROM FACTURA WHERE id = p_id_factura;
        IF v_estado <> 'PENDIENTE' THEN
            RAISE_APPLICATION_ERROR(-20400,
                'Solo se pueden agregar detalles a facturas PENDIENTES. Estado: ' || v_estado);
        END IF;
        INSERT INTO DETALLE_FACTURA (id_factura, tipo_concepto, descripcion,
                                     cantidad, precio_unitario, subtotal, id_medicamento)
        VALUES (p_id_factura, p_tipo_concepto, p_descripcion,
                p_cantidad, p_precio_unitario, ROUND(p_cantidad * p_precio_unitario, 2),
                p_id_medicamento)
        RETURNING id INTO p_id_detalle;
    END;

    PROCEDURE pagar_factura(p_id_factura IN NUMBER, p_metodo_pago IN VARCHAR2) IS
        v_estado FACTURA.estado_factura%TYPE;
    BEGIN
        SELECT estado_factura INTO v_estado FROM FACTURA WHERE id = p_id_factura FOR UPDATE;
        IF v_estado <> 'PENDIENTE' THEN
            RAISE_APPLICATION_ERROR(-20401,
                'Solo se pueden pagar facturas PENDIENTES. Estado: ' || v_estado);
        END IF;
        UPDATE FACTURA SET estado_factura = 'PAGADA', metodo_pago = p_metodo_pago
         WHERE id = p_id_factura;
        COMMIT;
    END;

    PROCEDURE anular_factura(p_id_factura IN NUMBER) IS
        v_estado FACTURA.estado_factura%TYPE;
    BEGIN
        SELECT estado_factura INTO v_estado FROM FACTURA WHERE id = p_id_factura FOR UPDATE;
        IF v_estado = 'ANULADA' THEN
            RAISE_APPLICATION_ERROR(-20402, 'La factura ya está anulada.');
        END IF;
        FOR det IN (SELECT id_medicamento, cantidad FROM DETALLE_FACTURA
                     WHERE id_factura = p_id_factura AND id_medicamento IS NOT NULL)
        LOOP
            UPDATE MEDICAMENTO SET stock_disponible = stock_disponible + ROUND(det.cantidad)
             WHERE id = det.id_medicamento;
        END LOOP;
        UPDATE FACTURA SET estado_factura = 'ANULADA' WHERE id = p_id_factura;
        COMMIT;
    EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE;
    END;

    PROCEDURE factura_rapida(
        p_cedula_prop IN VARCHAR2, p_id_paciente IN NUMBER,
        p_tipo_concepto IN VARCHAR2, p_descripcion IN VARCHAR2,
        p_precio IN NUMBER, p_metodo_pago IN VARCHAR2,
        p_id_medicamento IN NUMBER DEFAULT NULL, p_id_factura OUT NUMBER
    ) IS
        v_det NUMBER;
    BEGIN
        crear_factura(p_cedula_prop, p_id_paciente, p_metodo_pago, p_id_factura);
        agregar_detalle(p_id_factura, p_tipo_concepto, p_descripcion,
                        1, p_precio, p_id_medicamento, v_det);
        pagar_factura(p_id_factura, p_metodo_pago);
    EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE;
    END;

    FUNCTION ingresos_periodo(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT NVL(SUM(total), 0) INTO v_total FROM FACTURA
         WHERE estado_factura = 'PAGADA'
           AND TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta;
        RETURN v_total;
    END;

END PKG_FACTURACION;
/

PROMPT '[OK] PKG_FACTURACION creado.';

CREATE OR REPLACE PACKAGE PKG_INVENTARIO AS
    PROCEDURE reponer_medicamento(p_id_medicamento IN NUMBER, p_cantidad IN NUMBER);
    PROCEDURE reponer_vacuna(p_id_vacuna IN NUMBER, p_cantidad IN NUMBER);
    FUNCTION  valor_inventario_medicamentos RETURN NUMBER;
    FUNCTION  items_proximos_vencer(p_dias IN NUMBER DEFAULT 30) RETURN NUMBER;
    PROCEDURE alertar_stock_bajo(p_umbral IN NUMBER DEFAULT 10);
END PKG_INVENTARIO;
/

CREATE OR REPLACE PACKAGE BODY PKG_INVENTARIO AS

    PROCEDURE reponer_medicamento(p_id_medicamento IN NUMBER, p_cantidad IN NUMBER) IS
    BEGIN
        IF p_cantidad <= 0 THEN
            RAISE_APPLICATION_ERROR(-20500, 'La cantidad a reponer debe ser mayor a 0.');
        END IF;
        UPDATE MEDICAMENTO SET stock_disponible = stock_disponible + p_cantidad
         WHERE id = p_id_medicamento;
        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20501, 'Medicamento ID=' || p_id_medicamento || ' no encontrado.');
        END IF;
        COMMIT;
    END;

    PROCEDURE reponer_vacuna(p_id_vacuna IN NUMBER, p_cantidad IN NUMBER) IS
    BEGIN
        IF p_cantidad <= 0 THEN
            RAISE_APPLICATION_ERROR(-20502, 'La cantidad a reponer debe ser mayor a 0.');
        END IF;
        UPDATE VACUNA SET stock_disponible = stock_disponible + p_cantidad
         WHERE id = p_id_vacuna;
        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20503, 'Vacuna ID=' || p_id_vacuna || ' no encontrada.');
        END IF;
        COMMIT;
    END;

    FUNCTION valor_inventario_medicamentos RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT NVL(SUM(precio * stock_disponible), 0) INTO v_total FROM MEDICAMENTO;
        RETURN v_total;
    END;

    FUNCTION items_proximos_vencer(p_dias IN NUMBER DEFAULT 30) RETURN NUMBER IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count FROM (
            SELECT 1 FROM MEDICAMENTO
             WHERE fecha_vencimiento IS NOT NULL
               AND fecha_vencimiento BETWEEN SYSDATE AND SYSDATE + p_dias
            UNION ALL
            SELECT 1 FROM VACUNA
             WHERE fecha_vencimiento IS NOT NULL
               AND fecha_vencimiento BETWEEN SYSDATE AND SYSDATE + p_dias
        );
        RETURN v_count;
    END;

    PROCEDURE alertar_stock_bajo(p_umbral IN NUMBER DEFAULT 10) IS
    BEGIN
        FOR r IN (
            SELECT 'MEDICAMENTO' AS tipo, nombre, stock_disponible FROM MEDICAMENTO WHERE stock_disponible <= p_umbral
            UNION ALL
            SELECT 'VACUNA',              nombre, stock_disponible FROM VACUNA      WHERE stock_disponible <= p_umbral
            ORDER BY stock_disponible
        ) LOOP
            DBMS_OUTPUT.PUT_LINE('[ALERTA STOCK] ' || r.tipo ||
                ': "' || r.nombre || '" — ' || r.stock_disponible || ' unidades.');
        END LOOP;
    END;

END PKG_INVENTARIO;
/

PROMPT '[OK] PKG_INVENTARIO creado.';

CREATE OR REPLACE PACKAGE PKG_REPORTES AS
    FUNCTION consultas_por_vet(p_cedula_vet IN VARCHAR2, p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER;
    FUNCTION ingresos_por_tipo(p_tipo_concepto IN VARCHAR2, p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER;
    FUNCTION paciente_mas_activo(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER;
    FUNCTION tasa_cancelacion(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER;
    PROCEDURE resumen_general;
END PKG_REPORTES;
/

CREATE OR REPLACE PACKAGE BODY PKG_REPORTES AS

    FUNCTION consultas_por_vet(p_cedula_vet IN VARCHAR2, p_desde IN DATE, p_hasta IN DATE)
    RETURN NUMBER IS v_count NUMBER; BEGIN
        SELECT COUNT(*) INTO v_count FROM CONSULTA
         WHERE cedula_veterinario = p_cedula_vet
           AND TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta;
        RETURN v_count;
    END;

    FUNCTION ingresos_por_tipo(p_tipo_concepto IN VARCHAR2, p_desde IN DATE, p_hasta IN DATE)
    RETURN NUMBER IS v_total NUMBER; BEGIN
        SELECT NVL(SUM(df.subtotal), 0) INTO v_total
          FROM DETALLE_FACTURA df JOIN FACTURA f ON df.id_factura = f.id
         WHERE f.estado_factura = 'PAGADA'
           AND UPPER(df.tipo_concepto) = UPPER(p_tipo_concepto)
           AND TRUNC(CAST(f.fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta;
        RETURN v_total;
    END;

    FUNCTION paciente_mas_activo(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER IS
        v_id NUMBER;
    BEGIN
        SELECT id_paciente INTO v_id FROM (
            SELECT id_paciente, COUNT(*) AS visitas FROM (
                SELECT id_paciente FROM CONSULTA
                 WHERE TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta
                UNION ALL
                SELECT id_paciente FROM CIRUGIA
                 WHERE TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta
            ) GROUP BY id_paciente ORDER BY visitas DESC
        ) WHERE ROWNUM = 1;
        RETURN v_id;
    EXCEPTION WHEN NO_DATA_FOUND THEN RETURN NULL;
    END;

    FUNCTION tasa_cancelacion(p_desde IN DATE, p_hasta IN DATE) RETURN NUMBER IS
        v_total NUMBER; v_canceladas NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total      FROM CITA WHERE TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta;
        SELECT COUNT(*) INTO v_canceladas FROM CITA WHERE estado_cita = 'CANCELADA'
           AND TRUNC(CAST(fecha_hora AS DATE)) BETWEEN p_desde AND p_hasta;
        IF v_total = 0 THEN RETURN 0; END IF;
        RETURN ROUND((v_canceladas / v_total) * 100, 2);
    END;

    PROCEDURE resumen_general IS
        v_prop NUMBER; v_pac NUMBER; v_vet NUMBER; v_est NUMBER;
        v_citas NUMBER; v_fact NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_prop  FROM PROPIETARIO;
        SELECT COUNT(*) INTO v_pac   FROM PACIENTE;
        SELECT COUNT(*) INTO v_vet   FROM VETERINARIO WHERE activo = 1;
        SELECT COUNT(*) INTO v_est   FROM ESTILISTA   WHERE activo = 1;
        SELECT COUNT(*) INTO v_citas FROM CITA    WHERE estado_cita    = 'PROGRAMADA';
        SELECT COUNT(*) INTO v_fact  FROM FACTURA WHERE estado_factura = 'PENDIENTE';
        DBMS_OUTPUT.PUT_LINE('========= RESUMEN VETCARE =========');
        DBMS_OUTPUT.PUT_LINE('Propietarios        : ' || v_prop);
        DBMS_OUTPUT.PUT_LINE('Pacientes           : ' || v_pac);
        DBMS_OUTPUT.PUT_LINE('Veterinarios activos: ' || v_vet);
        DBMS_OUTPUT.PUT_LINE('Estilistas activos  : ' || v_est);
        DBMS_OUTPUT.PUT_LINE('Citas programadas   : ' || v_citas);
        DBMS_OUTPUT.PUT_LINE('Facturas pendientes : ' || v_fact);
        DBMS_OUTPUT.PUT_LINE('Valor inv. meds     : $' ||
            TO_CHAR(PKG_INVENTARIO.valor_inventario_medicamentos, '999,999,990.00'));
        DBMS_OUTPUT.PUT_LINE('Items por vencer 30d: ' || PKG_INVENTARIO.items_proximos_vencer(30));
        DBMS_OUTPUT.PUT_LINE('===================================');
    END;

END PKG_REPORTES;
/

PROMPT '[OK] PKG_REPORTES creado.';

CREATE OR REPLACE VIEW VW_CITAS_COMPLETA AS
    SELECT c.id, c.fecha_hora, c.tipo_cita, c.estado_cita, c.motivo,
           p.id            AS id_paciente,
           p.nombre        AS nombre_paciente,
           p.especie,
           pr.cedula       AS cedula_propietario,
           pr.nombre || ' ' || pr.apellido AS propietario,
           v.cedula        AS cedula_veterinario,
           v.nombre || ' ' || v.apellido   AS veterinario,
           s.id            AS id_solicitud,
           s.estado        AS estado_solicitud
      FROM CITA c
      JOIN PACIENTE      p  ON c.id_paciente        = p.id
      JOIN PROPIETARIO   pr ON p.cedula_propietario = pr.cedula
      JOIN VETERINARIO   v  ON c.cedula_veterinario = v.cedula
      LEFT JOIN SOLICITUD_CITA s ON c.id_solicitud_cita = s.id;

CREATE OR REPLACE VIEW VW_HISTORIAL_PACIENTE AS
    SELECT p.id AS id_paciente, p.nombre AS nombre_paciente, p.especie,
           'CONSULTA'    AS tipo_evento, c.id AS id_evento,
           c.fecha_hora, c.diagnostico AS detalle,
           v.nombre || ' ' || v.apellido AS profesional
      FROM CONSULTA c
      JOIN PACIENTE    p ON c.id_paciente        = p.id
      JOIN VETERINARIO v ON c.cedula_veterinario = v.cedula
    UNION ALL
    SELECT p.id, p.nombre, p.especie,
           'CIRUGIA', ci.id, ci.fecha_hora, ci.tipo_cirugia,
           v.nombre || ' ' || v.apellido
      FROM CIRUGIA ci
      JOIN PACIENTE    p ON ci.id_paciente        = p.id
      JOIN VETERINARIO v ON ci.cedula_veterinario = v.cedula
    UNION ALL
    SELECT p.id, p.nombre, p.especie,
           'VACUNACION', va.id, va.fecha_hora, vac.nombre,
           v.nombre || ' ' || v.apellido
      FROM VACUNACION va
      JOIN PACIENTE    p   ON va.id_paciente        = p.id
      JOIN VACUNA      vac ON va.id_vacuna           = vac.id
      JOIN VETERINARIO v   ON va.cedula_veterinario  = v.cedula
    UNION ALL
    SELECT p.id, p.nombre, p.especie,
           'INTERNACION', i.id, i.fecha_hora_ingreso, i.motivo,
           v.nombre || ' ' || v.apellido
      FROM INTERNACION i
      JOIN PACIENTE    p ON i.id_paciente        = p.id
      JOIN VETERINARIO v ON i.cedula_veterinario = v.cedula
    UNION ALL
    SELECT p.id, p.nombre, p.especie,
           'ESTETICA', se.id, se.fecha_hora, se.tipo_servicio,
           est.nombre || ' ' || est.apellido
      FROM SERVICIO_ESTETICO se
      JOIN PACIENTE  p   ON se.id_paciente     = p.id
      JOIN ESTILISTA est ON se.cedula_estilista = est.cedula;

CREATE OR REPLACE VIEW VW_INVENTARIO_ESTADO AS
    SELECT 'MEDICAMENTO' AS tipo, id, nombre, stock_disponible, fecha_vencimiento, precio,
           CASE
               WHEN fecha_vencimiento IS NOT NULL AND fecha_vencimiento < SYSDATE THEN 'VENCIDO'
               WHEN stock_disponible = 0   THEN 'SIN_STOCK'
               WHEN stock_disponible <= 5  THEN 'CRITICO'
               WHEN stock_disponible <= 10 THEN 'BAJO'
               ELSE 'DISPONIBLE'
           END AS estado
      FROM MEDICAMENTO
    UNION ALL
    SELECT 'VACUNA', id, nombre, stock_disponible, fecha_vencimiento, precio,
           CASE
               WHEN fecha_vencimiento IS NOT NULL AND fecha_vencimiento < SYSDATE THEN 'VENCIDA'
               WHEN stock_disponible = 0   THEN 'SIN_STOCK'
               WHEN stock_disponible <= 5  THEN 'CRITICO'
               WHEN stock_disponible <= 10 THEN 'BAJO'
               ELSE 'DISPONIBLE'
           END
      FROM VACUNA;

CREATE OR REPLACE VIEW VW_FACTURACION_PROPIETARIO AS
    SELECT pr.cedula,
           pr.nombre || ' ' || pr.apellido AS propietario,
           COUNT(f.id)  AS total_facturas,
           SUM(CASE WHEN f.estado_factura = 'PAGADA'    THEN 1 ELSE 0 END) AS pagadas,
           SUM(CASE WHEN f.estado_factura = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
           NVL(SUM(CASE WHEN f.estado_factura = 'PAGADA' THEN f.total END), 0) AS total_pagado
      FROM PROPIETARIO pr
      LEFT JOIN FACTURA f ON pr.cedula = f.cedula_propietario
     GROUP BY pr.cedula, pr.nombre, pr.apellido;

PROMPT '[OK] Sección 10 — 4 vistas creadas.';


-- ============================================================
-- SECCION 11 - DATOS DE EJEMPLO REALISTAS
-- Fecha de referencia: 2026-06-05  (hoy)
-- Cubre TODOS los estados posibles de cada entidad
-- ============================================================

PROMPT '======================================================';
PROMPT '  VetCare - Cargando datos de ejemplo realistas...';
PROMPT '======================================================';

-- ============================================================
-- USUARIOS
-- admin + 5 veterinarios (1 inactivo) + 3 estilistas + 1 recepcionista
-- ============================================================
INSERT INTO USUARIO (nombre_usuario, contrasena, rol, activo)
VALUES ('admin', '1234', 'ADMIN', 1);

INSERT INTO USUARIO VALUES ('fmartinez',   'vet2024',   'VETERINARIO',   1, NULL, NULL, '77123456');
INSERT INTO USUARIO VALUES ('svargas',     'cirugia24', 'VETERINARIO',   1, NULL, NULL, '77234567');
INSERT INTO USUARIO VALUES ('rbermudez',   'cardio24',  'VETERINARIO',   1, NULL, NULL, '77345678');
INSERT INTO USUARIO VALUES ('pquintero',   'derm2024',  'VETERINARIO',   1, NULL, NULL, '77456789');
INSERT INTO USUARIO VALUES ('jospina',     'vet2024',   'VETERINARIO',   0, NULL, NULL, '77567890');
INSERT INTO USUARIO VALUES ('recepcion01', 'recep2024', 'RECEPCIONISTA', 1, NULL, NULL, NULL);
INSERT INTO USUARIO VALUES ('lherrera',    'estil2024', 'ESTILISTA',     1, NULL, NULL, '88123456');
INSERT INTO USUARIO VALUES ('dmorales',    'estil2024', 'ESTILISTA',     1, NULL, NULL, '88234567');
INSERT INTO USUARIO VALUES ('cacosta',     'estil2024', 'ESTILISTA',     1, NULL, NULL, '88345678');
COMMIT;
PROMPT '[OK] Usuarios (10: admin + 4 vets activos + 1 vet inactivo + rec + 3 estilistas).';

-- ============================================================
-- PROPIETARIOS (10)
-- ============================================================
INSERT INTO PROPIETARIO VALUES ('1065812345','Maria',    'Rodriguez','3105551234','maria.rodriguez@gmail.com',   'Cl 15 #23-45, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065823456','Carlos',   'Mendoza',  '3115552345','carlos.mendoza@hotmail.com',  'Cra 19 #12-30, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065834567','Ana',      'Castro',   '3125553456','ana.castro@outlook.com',      'Cl 20 #15-10, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065845678','Jorge',    'Pinto',    '3135554567','jorge.pinto@yahoo.com',       'Cra 8 #25-40, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065856789','Laura',    'Gomez',    '3145555678','laura.gomez@gmail.com',       'Cl 30 #18-22, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065867890','Sebastian','Ramirez',  '3155556789','sebastian.ramirez@gmail.com', 'Cra 12 #5-67, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065878901','Valentina','Diaz',     '3165557890','valentina.diaz@hotmail.com',  'Cl 7 #14-25, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065889012','Andres',   'Lopez',    '3175558901','andres.lopez@gmail.com',      'Cra 22 #30-15, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065900001','Natalia',  'Vargas',   '3180011001','natalia.vargas@gmail.com',    'Cl 40 #10-15, Valledupar');
INSERT INTO PROPIETARIO VALUES ('1065900002','Hector',   'Suarez',   '3180022002','hector.suarez@hotmail.com',   'Cra 5 #8-30, Valledupar');
COMMIT;
PROMPT '[OK] Propietarios (10).';

-- ============================================================
-- VETERINARIOS (5: 4 activos + 1 inactivo para pruebas de usuario inactivo)
-- ============================================================
INSERT INTO VETERINARIO VALUES ('77123456','Felipe',  'Martinez','3201234567','felipe.martinez@vetcare.com',  'Medicina General','MV-2018-7845',1);
INSERT INTO VETERINARIO VALUES ('77234567','Sandra',  'Vargas',  '3202345678','sandra.vargas@vetcare.com',    'Cirugia',         'MV-2019-8923',1);
INSERT INTO VETERINARIO VALUES ('77345678','Ricardo', 'Bermudez','3203456789','ricardo.bermudez@vetcare.com', 'Cardiologia',     'MV-2017-6731',1);
INSERT INTO VETERINARIO VALUES ('77456789','Patricia','Quintero','3204567890','patricia.quintero@vetcare.com','Dermatologia',    'MV-2020-9456',1);
INSERT INTO VETERINARIO VALUES ('77567890','Jorge',   'Ospina',  '3205678901','jorge.ospina@vetcare.com',     'Medicina General','MV-2021-1032',0);
COMMIT;

-- ============================================================
-- ESTILISTAS (3 activos)
-- ============================================================
INSERT INTO ESTILISTA VALUES ('88123456','Lucia',   'Herrera','3211234567','lucia.herrera@vetcare.com',   'Razas peque',  1);
INSERT INTO ESTILISTA VALUES ('88234567','Daniel',  'Morales','3212345678','daniel.morales@vetcare.com',  'Razas grandes',1);
INSERT INTO ESTILISTA VALUES ('88345678','Carolina','Acosta', '3213456789','carolina.acosta@vetcare.com', 'Estetica avanz',1);
COMMIT;
PROMPT '[OK] Veterinarios (5) y Estilistas (3).';

-- ============================================================
-- MEDICAMENTOS (14)
-- Stocks iniciales contemplan las unidades que TRG_DETALLE_STOCK_MEDICAMENTO
-- descontara al insertar detalles de factura con tipo_concepto=MEDICAMENTO:
--   Med  1 Amoxicilina   : inicial 81, -1 en F9  => final 80
--   Med  4 Drontal Plus  : inicial 121, -1 en F1  => final 120
--   Med  6 Omeprazol     : inicial 102, -2 en F3,F5 => final 100
--   Med  9 Apoquel       : inicial 19,  -1 en F8  => final 18
-- Med 11 Dexametasona : stock 4  (CRITICO <= 5)
-- Med 12 Metronidazol : stock 0  (SIN STOCK)
-- Med 13 Fenobarbital : stock 3  (CRITICO, proximo vencer 2026-08-10)
-- Med 14 Ketoconazol  : stock 0, vencido 2025-12-01 (VENCIDO + SIN STOCK)
-- ============================================================
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Amoxicilina',   'Antibiotico amplio espectro para infecciones bacterianas',   'Bayer',       18500,  81, DATE '2027-06-30', '250 mg',        'Antibiotico');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Meloxicam',     'Antiinflamatorio no esteroideo para dolor y fiebre',         'Boehringer',  22000,  60, DATE '2027-09-15', '1.5 mg/ml',     'Analgesico');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Bravecto',      'Antiparasitario externo masticable accion 3 meses',          'MSD',         85000,  35, DATE '2028-01-20', '500 mg',        'Antiparasitario');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Drontal Plus',  'Desparasitante interno tenias y nematodos',                  'Bayer',       12000, 121, DATE '2027-11-10', '50/144/150 mg', 'Antiparasitario');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Prednisolona',  'Corticosteroide para inflamacion y alergias',                'Pfizer',      15500,  45, DATE '2027-08-05', '5 mg',          'Corticosteroide');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Omeprazol',     'Inhibidor bomba protones protector gastrico',                'Genfar',       9500, 102, DATE '2028-03-12', '20 mg',         'Gastroprotector');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Tramadol',      'Analgesico opioide para dolor moderado a severo',            'Pfizer',      28000,  25, DATE '2027-07-22', '50 mg',         'Analgesico');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Cefalexina',    'Antibiotico cefalosporina de primera generacion',            'MSD',         26000,  50, DATE '2027-10-18', '500 mg',        'Antibiotico');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Apoquel',       'Antipruriginoso para control de alergias cronicas',          'Zoetis',     145000,  19, DATE '2027-05-30', '16 mg',         'Inmunomodulador');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Lactato Ringer','Solucion electrolitica IV para hidratacion parenteral',      'Baxter',      18000,  40, DATE '2027-12-01', '500 ml',        'Fluido IV');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Dexametasona',  'Corticosteroide potente choque anafilactico y edemas',       'Genfar',      12500,   4, DATE '2027-03-15', '2 mg/ml',       'Corticosteroide');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Metronidazol',  'Antiparasitario y antibacteriano anaerobico',                'Mk',           8500,   0, DATE '2027-04-20', '250 mg',        'Antibiotico');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Fenobarbital',  'Anticonvulsivante para epilepsia canina',                    'Pfizer',      45000,   3, DATE '2026-08-10', '100 mg',        'Anticonvulsivante');
INSERT INTO MEDICAMENTO (nombre,descripcion,fabricante,precio,stock_disponible,fecha_vencimiento,concentracion,categoria)
VALUES ('Ketoconazol',   'Antifungico topico VENCIDO retirar de inventario',           'Mk',          35000,   0, DATE '2025-12-01', '200 mg champu', 'Antifungico');
COMMIT;
PROMPT '[OK] Medicamentos (14: 10 normal, 2 critico-stock, 1 sin-stock, 1 vencido).';

-- ============================================================
-- VACUNAS (10)
-- TRG_VACUNACION_STOCK decrementa 1 dosis por cada INSERT en VACUNACION.
-- Stocks iniciales calculados para dejar valores finales realistas:
--   Vanguard(1)  : 30 - 4 apps = 26 final  | Nobivac(2): 50 - 4 = 46
--   TripleFel(3) : 25 - 2 apps = 23 final  | Bordetella(4): 20 - 1 = 19
--   Leucemia(5)  : 15 - 2 apps = 13 final  | Parvovirus(6): 40 - 4 = 36
--   Moquillo(7)  : 35 - 1 app  = 34 final  | Giardia(8): 20 - 2 = 18
--   Hexaval(9)   :  3 - 1 app  =  2 final  (CRITICO)
--   RabiaFel(10) :  0 vencida 2025-06-01   (SIN STOCK + VENCIDA)
-- ============================================================
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Vanguard Plus 5', 'Zoetis',    'VG-2026-A12', 45000, 30, DATE '2027-04-15');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Nobivac Rabia',   'MSD',       'NB-2026-R03', 35000, 50, DATE '2027-06-20');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Triple Felina',   'Virbac',    'VR-2026-F08', 42000, 25, DATE '2027-05-10');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Bordetella',      'Zoetis',    'VG-2026-B05', 38000, 20, DATE '2027-08-12');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Leucemia Felina', 'Boehringer','BH-2026-L02', 55000, 15, DATE '2027-07-08');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Parvovirus',      'MSD',       'NB-2026-P11', 32000, 40, DATE '2027-09-25');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Moquillo',        'Zoetis',    'VG-2026-M09', 34000, 35, DATE '2027-10-05');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Giardia',         'Virbac',    'VR-2026-G04', 48000, 20, DATE '2027-06-30');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Hexavalente',     'MSD',       'NB-2026-H01', 65000,  3, DATE '2026-09-10');
INSERT INTO VACUNA (nombre,laboratorio,lote,precio,stock_disponible,fecha_vencimiento) VALUES ('Rabia Felina',    'Virbac',    'VR-2024-RF5', 38000,  0, DATE '2025-06-01');
COMMIT;
PROMPT '[OK] Vacunas (10: 7 normal, 1 critico-stock, 1 vencida-sin-stock).';

-- ============================================================
-- PACIENTES (20)  IDs 1-20 asignados por SEQ_PACIENTE
-- ============================================================
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Rocky',  'Canino','Labrador',        'Macho',  28.50, DATE '2020-03-15','CHIP001-2020','1065812345');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Luna',   'Felino','Persa',            'Hembra',  4.20, DATE '2021-07-22','CHIP002-2021','1065812345');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Kira',   'Canino','Border Collie',    'Hembra', 18.40, DATE '2021-03-08','CHIP003-2021','1065812345');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Max',    'Canino','Golden Retriever', 'Macho',  32.00, DATE '2019-11-08','CHIP004-2019','1065823456');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Coco',   'Canino','French Poodle',    'Hembra',  6.80, DATE '2022-01-30','CHIP005-2022','1065823456');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Oreo',   'Felino','Comun Europeo',    'Macho',   4.50, DATE '2022-06-14','CHIP006-2022','1065823456');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Milo',   'Felino','Siames',           'Macho',   3.90, DATE '2023-04-12','CHIP007-2023','1065834567');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Bella',  'Canino','Schnauzer',        'Hembra',  9.50, DATE '2021-09-05','CHIP008-2021','1065834567');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Toby',   'Canino','Beagle',           'Macho',  12.30, DATE '2020-06-18','CHIP009-2020','1065845678');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Nala',   'Felino','Bengali',          'Hembra',  4.70, DATE '2022-08-25','CHIP010-2022','1065845678');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Simba',  'Canino','Pastor Aleman',    'Macho',  35.50, DATE '2018-12-10','CHIP011-2018','1065856789');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Mia',    'Felino','Comun Europeo',    'Hembra',  3.50, DATE '2023-02-14','CHIP012-2023','1065856789');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Bruno',  'Canino','Bulldog Frances',  'Macho',  13.80, DATE '2020-05-20','CHIP013-2020','1065867890');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Lola',   'Canino','Chihuahua',        'Hembra',  2.30, DATE '2022-10-08','CHIP014-2022','1065867890');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Zeus',   'Canino','Rottweiler',       'Macho',  42.00, DATE '2019-08-12','CHIP015-2019','1065878901');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Daisy',  'Canino','Yorkshire',        'Hembra',  3.10, DATE '2022-11-30','CHIP016-2022','1065878901');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Pelusa', 'Felino','Maine Coon',       'Hembra',  5.80, DATE '2020-09-15','CHIP017-2020','1065889012');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Thor',   'Canino','Husky Siberiano',  'Macho',  27.50, DATE '2020-04-22','CHIP018-2020','1065889012');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Bolt',   'Canino','Dalmata',          'Macho',  22.00, DATE '2022-03-10','CHIP019-2022','1065900001');
INSERT INTO PACIENTE (nombre,especie,raza,sexo,peso,fecha_nacimiento,microchip,cedula_propietario) VALUES ('Perla',  'Felino','Angora',           'Hembra',  3.80, DATE '2023-08-05', NULL,          '1065900001');
COMMIT;
PROMPT '[OK] Pacientes (20).';

-- ============================================================
-- SOLICITUDES DE CITA (9) — PENDIENTE(4) ACEPTADA(3) RECHAZADA(2)
-- ============================================================
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,raza,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Diana Rojas',    '3201112233','diana.rojas@gmail.com',    'Firulais','Canino','Mestizo','Tos persistente hace 4 dias',               DATE '2026-06-10','09:00','PENDIENTE', TIMESTAMP '2026-06-03 18:30:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Manuel Torres',  '3202223344','manuel.torres@hotmail.com','Misifu',  'Felino','Vacunacion anual felina',                              DATE '2026-06-11','10:30','PENDIENTE', TIMESTAMP '2026-06-03 09:15:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Carolina Silva', '3203334455','carolina.silva@gmail.com', 'Pepe',    'Canino','Revision dental y limpieza bucal',                     DATE '2026-06-14','14:00','PENDIENTE', TIMESTAMP '2026-06-04 14:45:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Hector Suarez',  '3180022002','hector.suarez@hotmail.com','Rex',     'Canino','Control peso y articulaciones Gran Danes',             DATE '2026-06-12','09:00','PENDIENTE', TIMESTAMP '2026-06-04 20:00:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Jorge Pinto',    '3135554567','jorge.pinto@yahoo.com',    'Toby',    'Canino','Evaluacion articulaciones Beagle',                     DATE '2026-05-22','11:00','ACEPTADA',  TIMESTAMP '2026-05-20 10:00:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Natalia Vargas', '3180011001','natalia.vargas@gmail.com', 'Bolt',    'Canino','Primera consulta Dalmata 4 anos',                      DATE '2026-05-30','09:00','ACEPTADA',  TIMESTAMP '2026-05-28 08:00:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Andres Lopez',   '3175558901','andres.lopez@gmail.com',   'Thor',    'Canino','Seguimiento cardiologico mensual',                     DATE '2026-05-25','11:00','ACEPTADA',  TIMESTAMP '2026-05-23 07:30:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Esteban Marin',  '3206667788','esteban.marin@outlook.com','Bolt',    'Canino','Consulta sabado por la tarde',                         DATE '2026-05-09','15:00','RECHAZADA', TIMESTAMP '2026-05-07 12:30:00');
INSERT INTO SOLICITUD_CITA (nombre_propietario,telefono,correo,nombre_mascota,especie,motivo,fecha,hora,estado,fecha_solicitud)
VALUES ('Maria Rodriguez','3105551234','maria.rodriguez@gmail.com','Rocky',   'Canino','Control post-vacunacion fuera de horario',             DATE '2026-03-20','08:00','RECHAZADA', TIMESTAMP '2026-03-18 16:00:00');
COMMIT;
PROMPT '[OK] Solicitudes (9: 4 PENDIENTE, 3 ACEPTADA, 2 RECHAZADA).';

-- ============================================================
-- CITAS (25) — TRG_CITA_ESTADO solo valida UPDATE, no INSERT.
-- Se insertan con estado final directamente.
-- Citas 1-12: REALIZADA | 13-14: CANCELADA | 15-16: EN_CURSO | 17-25: PROGRAMADA
-- ============================================================
-- Historicas completadas (12)
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (1, '77123456',TIMESTAMP '2026-03-10 08:00:00','CONSULTA_GENERAL','REALIZADA','Control anual de salud Rocky',            'Paciente en excelente estado');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (2, '77456789',TIMESTAMP '2026-03-15 09:30:00','DERMATOLOGIA',   'REALIZADA','Picazon constante en orejas',             'Otitis externa tratamiento iniciado');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (4, '77123456',TIMESTAMP '2026-04-05 10:00:00','CONSULTA_GENERAL','REALIZADA','Vomito y decaimiento 3 dias',            'Gastroenteritis con deshidratacion leve');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (7, '77123456',TIMESTAMP '2026-04-10 11:00:00','VACUNACION',     'REALIZADA','Refuerzo anual felino',                  NULL);
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (9, '77234567',TIMESTAMP '2026-04-15 14:00:00','CIRUGIA',        'REALIZADA','Esterilizacion programada Toby',         'Sin complicaciones postoperatorias');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (11,'77345678',TIMESTAMP '2026-04-20 08:30:00','CARDIOLOGIA',    'REALIZADA','Soplo cardiaco detectado mes anterior',  'Evaluacion ecocardiografica completa');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (8, '77123456',TIMESTAMP '2026-05-03 15:00:00','CONSULTA_GENERAL','REALIZADA','Dificultad respiratoria al ejercicio',  'Sindrome braquicefalico leve');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (15,'77234567',TIMESTAMP '2026-05-08 09:00:00','CIRUGIA',        'REALIZADA','Masa subcutanea 2cm flanco derecho',    'Biopsia enviada al laboratorio');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (17,'77456789',TIMESTAMP '2026-05-12 10:30:00','DERMATOLOGIA',   'REALIZADA','Caida de pelo region dorsal',           'Champu medicado + Apoquel');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (3, '77123456',TIMESTAMP '2026-05-15 16:00:00','CONSULTA_GENERAL','REALIZADA','Cojera pata trasera derecha',           'Esguince leve articulacion tarsal');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (5, '77123456',TIMESTAMP '2026-05-20 08:00:00','VACUNACION',     'REALIZADA','Refuerzo Vanguard Plus anual',          NULL);
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (18,'77345678',TIMESTAMP '2026-05-22 11:30:00','CARDIOLOGIA',    'REALIZADA','Seguimiento cardiologico mensual Thor', 'Mejoria sostenida mantener tratamiento');
-- Canceladas (2)
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (10,'77456789',TIMESTAMP '2026-05-01 14:30:00','DERMATOLOGIA',   'CANCELADA','Acaros en orejas y rascado facial',     'Cliente no se presento. Reprogramar.');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (13,'77123456',TIMESTAMP '2026-05-10 14:00:00','CONSULTA_GENERAL','CANCELADA','Control de peso mensual Bruno',         'Cancelada por propietario viaje trabajo');
-- En curso hoy 2026-06-05 (2)
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (1, '77123456',TIMESTAMP '2026-06-05 08:00:00','CONSULTA_GENERAL','REALIZADA','Control semestral Rocky',              'Propietario presente en sala');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (8, '77456789',TIMESTAMP '2026-06-05 10:00:00','DERMATOLOGIA',   'EN_CURSO', 'Seguimiento alergias Bella',           'Segundo control post-tratamiento Apoquel');
-- Programadas futuras (9)
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (4, '77123456',TIMESTAMP '2026-06-10 08:00:00','CONSULTA_GENERAL','PROGRAMADA','Control anual Max Golden Retriever');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (16,'77456789',TIMESTAMP '2026-06-11 09:30:00','DERMATOLOGIA',   'PROGRAMADA','Primera revision dermatologica Daisy');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo,observaciones) VALUES (9, '77234567',TIMESTAMP '2026-06-12 14:00:00','CIRUGIA',        'PROGRAMADA','Artroscopia exploracion cadera Toby',   'Confirmar ayuno 12h previas. Analisis OK.');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (11,'77345678',TIMESTAMP '2026-06-15 08:30:00','CARDIOLOGIA',    'PROGRAMADA','Control ICC Simba mensual');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (14,'77123456',TIMESTAMP '2026-06-16 10:00:00','CONSULTA_GENERAL','PROGRAMADA','Chequeo general Lola Chihuahua');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (18,'77345678',TIMESTAMP '2026-06-18 11:00:00','CARDIOLOGIA',    'PROGRAMADA','Control mensual ICC Thor');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (19,'77123456',TIMESTAMP '2026-06-20 09:00:00','VACUNACION',     'PROGRAMADA','Hexavalente Bolt primer ano');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (20,'77456789',TIMESTAMP '2026-06-22 15:00:00','DERMATOLOGIA',   'PROGRAMADA','Primera consulta Perla Angora');
INSERT INTO CITA (id_paciente,cedula_veterinario,fecha_hora,tipo_cita,estado_cita,motivo)                VALUES (6, '77123456',TIMESTAMP '2026-07-02 08:00:00','CONSULTA_GENERAL','PROGRAMADA','Control anual Oreo felino');
COMMIT;
PROMPT '[OK] Citas (25: 12 REALIZADA, 2 CANCELADA, 2 EN_CURSO, 9 PROGRAMADA).';

-- ============================================================
-- CONSULTAS (15)
-- TRG_CONSULTA_SYNC_CITA: PROGRAMADA->EN_CURSO al crear consulta.
-- Con citas ya en estado REALIZADA el trigger no altera nada.
-- Consultas 1-12: vinculadas a citas 1-12. | 13-15: walk-in sin cita.
-- ============================================================
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (1, 1,'77123456',TIMESTAMP '2026-03-10 08:05:00',
    'Control de rutina anual. Sin quejas. Apetito y energia normales.',
    'Paciente sano. Condicion corporal 4/5. Denticion buena.',
    'Drontal Plus dosis unica desparasitacion. Proxima consulta 6 meses.',
    60000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (2, 2,'77456789',TIMESTAMP '2026-03-15 09:35:00',
    'Rascado constante orejas, sacude la cabeza, mal olor auricular bilateral.',
    'Otitis externa bilateral micotica por Candida albicans.',
    'Otomax 5 gotas c/12h x 10 dias. Limpieza auricular semanal con Otoclean.',
    75000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (3, 4,'77123456',TIMESTAMP '2026-04-05 10:05:00',
    'Vomito 4 veces/dia, anorexia, decaimiento y deshidratacion leve desde 3 dias.',
    'Gastroenteritis aguda con deshidratacion moderada 5%.',
    'Dieta blanda 5 dias. Omeprazol 20mg c/24h x 5 dias. Hidratacion IV 24h.',
    65000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (4, 7,'77123456',TIMESTAMP '2026-04-10 11:05:00',
    'Control pre-vacunal felino. Sin sintomas. Activo y con buen apetito.',
    'Apto para vacunacion. Estado general excelente.',
    'Aplicacion Triple Felina + Leucemia Felina. Proxima dosis 2027-04-10.',
    45000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (5, 9,'77234567',TIMESTAMP '2026-04-15 14:05:00',
    'Pre-quirurgico esterilizacion. Ayuno 12h cumplido. Examen fisico sin hallazgos.',
    'Apto para cirugia. ASA I. Constantes vitales normales. Hematologia OK.',
    'Proceder con ovariohisterectomia bajo anestesia general isoflurano.',
    80000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (6,11,'77345678',TIMESTAMP '2026-04-20 08:35:00',
    'Tos nocturna, intolerancia al ejercicio, epigastrio distendido, sincope breve hace 2 dias.',
    'Insuficiencia cardiaca congestiva grado II. Insuficiencia mitral severa por ecocardiografia.',
    'Enalapril 5mg c/12h. Furosemida 2mg/kg c/24h. Reposo. Control 4 semanas.',
    120000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (7, 8,'77123456',TIMESTAMP '2026-05-03 15:05:00',
    'Respiracion ruidosa con estridor, ronquidos intensos, intolerancia al calor.',
    'Sindrome braquicefalico leve. Narinas estenosis grado I.',
    'Control de peso estricto. Evitar ejercicio en calor. Revision en 3 meses.',
    70000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (8,15,'77234567',TIMESTAMP '2026-05-08 09:05:00',
    'Masa subcutanea 2cm diametro flanco derecho, movil, no dolorosa, 3 semanas evolucion.',
    'Lipoma subcutaneo probable. Cirugia de extraccion con biopsia indicada.',
    'Extraccion quirurgica + biopsia histopatologica. Resultados en 7 dias.',
    90000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (9,17,'77456789',TIMESTAMP '2026-05-12 10:35:00',
    'Alopecia progresiva en region dorsal y cuello, eritema, prurito moderado.',
    'Dermatitis atopica canina. TSH normal, descartado hipotiroidismo.',
    'Apoquel 16mg c/24h x 30 dias. Champu Malaseb 2 veces/semana. Control 4 semanas.',
    110000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (10, 3,'77123456',TIMESTAMP '2026-05-15 16:05:00',
    'Cojera grado II pata trasera derecha, inicio brusco tras carrera, sin herida aparente.',
    'Esguince grado I articulacion tibiotarsal derecha. Rx descarta fractura.',
    'Reposo absoluto 7 dias. Meloxicam 1.5mg/ml c/24h x 5 dias. Control 10 dias.',
    75000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (11, 5,'77123456',TIMESTAMP '2026-05-20 08:05:00',
    'Control pre-vacunal canino. Sin sintomas. Buen estado general.',
    'Apto para vacunacion. Condicion corporal optima 4.5/5.',
    'Aplicacion Vanguard Plus 5. Proxima dosis 2027-05-20.',
    40000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (12,18,'77345678',TIMESTAMP '2026-05-22 11:35:00',
    'Mejoria evidente: tos reducida 80%, tolera ejercicio leve 20min. Propietario satisfecho.',
    'ICC controlada. Fraccion de eyeccion estable 52%. Sin edema activo.',
    'Mantener Enalapril + Furosemida. Ecocardiograma control en 60 dias.',
    95000);
-- Walk-in sin cita (3)
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (NULL,12,'77456789',TIMESTAMP '2026-05-25 10:00:00',
    'Ojo derecho rojo, secrecion mucopurulenta, fotofobia leve desde 2 dias. Sin traumatismo.',
    'Conjuntivitis bacteriana unilateral ojo derecho.',
    'Tobramicina colirio 0.3% 2 gotas c/8h x 7 dias. Control si no mejora en 5 dias.',
    55000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (NULL,16,'77123456',TIMESTAMP '2026-05-28 12:00:00',
    'Vomito x5 en 6h, diarrea blanda amarilla, decaimiento marcado, sin apetito desde ayer.',
    'Gastroenteritis aguda con deshidratacion moderada 8%.',
    'Metoclopramida SQ. Hidratacion IV Lactato Ringer. Dieta blanda 5 dias. Omeprazol 20mg c/24h.',
    65000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (NULL,13,'77123456',TIMESTAMP '2026-06-01 11:00:00',
    'Propietario consulta por sobrepeso progresivo. CC 4.5/5. Dificultad para levantarse.',
    'Obesidad grado II en Bulldog Frances. Articulaciones en riesgo. Sin patologia activa.',
    'Dieta Hills Metabolic. Restriccion calorica 20%. Paseos cortos 2x/dia. Control 4 semanas.',
    60000);
INSERT INTO CONSULTA (id_cita,id_paciente,cedula_veterinario,fecha_hora,sintomas,diagnostico,tratamiento,costo)
VALUES (15, 1,'77123456',TIMESTAMP '2026-06-05 08:05:00',
    'Control semestral. Propietario refiere buen estado general. Sin quejas. Apetito y energia normales.',
    'Paciente sano. CC 4/5. Peso estable 28kg. Sarro dental leve grado I. Sin hallazgos patologicos.',
    'Desparasitacion Drontal Plus dosis unica. Limpieza dental recomendada. Proxima consulta control anual 2027-03.',
    60000);
COMMIT;
PROMPT '[OK] Consultas (16: 12 con cita + 3 walk-in sin cita + 1 control semestral Rocky).';

-- ============================================================
-- INTERNACIONES (5: 3 cerradas + 2 abiertas en curso)
-- ============================================================
INSERT INTO INTERNACION (id_paciente,cedula_veterinario,id_consulta,fecha_hora_ingreso,fecha_hora_egreso,
    motivo,diagnostico,costo_diario,observaciones,costo_medicamentos,medicamentos_detalle)
VALUES (4,'77123456',3,
    TIMESTAMP '2026-04-05 10:30:00', TIMESTAMP '2026-04-07 16:00:00',
    'Gastroenteritis con deshidratacion moderada',
    'Gastroenteritis aguda. Evolucion favorable. Alta dia 2.',
    85000,'Hidratacion IV Lactato Ringer. Monitoreo electrolitico. Dieta blanda progresiva.',
    36000,'Lactato Ringer 500ml x2, Omeprazol 20mg VO x2 dias');
INSERT INTO INTERNACION (id_paciente,cedula_veterinario,id_consulta,fecha_hora_ingreso,fecha_hora_egreso,
    motivo,diagnostico,costo_diario,observaciones,costo_medicamentos,medicamentos_detalle)
VALUES (9,'77234567',5,
    TIMESTAMP '2026-04-15 13:00:00', TIMESTAMP '2026-04-16 11:00:00',
    'Postoperatorio ovariohisterectomia',
    'Recuperacion sin complicaciones. Alta 22h post-cirugia.',
    75000,'Collar isabelino 10 dias. Tramadol SQ c/8h primeras 12h. Vigilar herida.',
    28000,'Tramadol 50mg SQ x3, Amoxicilina 250mg VO x5 dias');
INSERT INTO INTERNACION (id_paciente,cedula_veterinario,id_consulta,fecha_hora_ingreso,fecha_hora_egreso,
    motivo,diagnostico,costo_diario,observaciones,costo_medicamentos,medicamentos_detalle)
VALUES (11,'77345678',6,
    TIMESTAMP '2026-04-20 09:00:00', NULL,
    'Descompensacion insuficiencia cardiaca congestiva grado II',
    'ICC activa. Monitoreo continuo. Evolucion lenta pero favorable.',
    95000,'Oxigeno suplementario. ECG c/8h. Dieta hiposodica. Pesar diariamente.',
    52000,'Furosemida 20mg IV c/8h, Enalapril 5mg VO c/12h, Espironolactona 25mg VO c/24h');
INSERT INTO INTERNACION (id_paciente,cedula_veterinario,id_consulta,fecha_hora_ingreso,fecha_hora_egreso,
    motivo,diagnostico,costo_diario,observaciones,costo_medicamentos,medicamentos_detalle)
VALUES (16,'77123456',14,
    TIMESTAMP '2026-05-28 12:30:00', NULL,
    'Gastroenteritis severa con deshidratacion grado II',
    'En recuperacion. Tolerando agua oral. Vomitos reducidos. Pendiente alta.',
    75000,'Hidratacion IV mantenimiento. Monitoreo diuresis. Transicion dieta blanda.',
    24000,'Lactato Ringer 500ml x2, Metoclopramida 10mg SQ x2, Omeprazol 20mg VO');
INSERT INTO INTERNACION (id_paciente,cedula_veterinario,id_consulta,fecha_hora_ingreso,fecha_hora_egreso,
    motivo,diagnostico,costo_diario,observaciones,costo_medicamentos,medicamentos_detalle)
VALUES (13,'77123456',15,
    TIMESTAMP '2026-06-01 11:30:00', TIMESTAMP '2026-06-03 14:00:00',
    'Dieta de choque supervisada 48h por obesidad severa grado II',
    'Responde bien a restriccion calorica. Alta con plan dietetico escrito.',
    65000,'Actividad fisica leve supervisada. Control peso diario. Sin acceso a premios.',
    0, NULL);
COMMIT;
PROMPT '[OK] Internaciones (5: 3 CERRADAS, 2 ABIERTAS en curso).';

-- ============================================================
-- EXAMENES DE LABORATORIO (12)
-- Prioridades: NORMAL, URGENTE | Con y sin resultado
-- ============================================================
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (1,'77123456',1,TIMESTAMP '2026-03-10 08:30:00','Coproparasitologico','NORMAL',
    'Negativo para parasitos intestinales y protozoos.','Desparasitacion al dia. Repetir en 6 meses.',35000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (2,'77456789',2,TIMESTAMP '2026-03-15 10:00:00','Cultivo otico','NORMAL',
    'Candida albicans sensible a Clotrimazol y Nistatina.','Confirma otitis micotica. Tratamiento efectivo.',45000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (4,'77123456',3,TIMESTAMP '2026-04-05 11:00:00','Hemograma completo','NORMAL',
    'Leucocitosis leve 14.2x10^3/uL (ref 6-12). Resto normal.','Compatible con proceso infeccioso activo.',55000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (4,'77123456',3,TIMESTAMP '2026-04-05 11:15:00','Quimica sanguinea','NORMAL',
    'BUN 42 mg/dL (ref 7-27), Creatinina 1.8 mg/dL (ref 0.5-1.5).','Elevacion leve por deshidratacion. Normalizar con hidratacion.',68000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (11,'77345678',6,TIMESTAMP '2026-04-20 09:00:00','Ecocardiograma','URGENTE',
    'Insuficiencia mitral severa. FE 52% (limite inferior). AI dilatada.','Pronostico reservado. Tratamiento intensivo inmediato.',180000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (11,'77345678',6,TIMESTAMP '2026-04-20 09:30:00','Electrocardiograma','URGENTE',
    'Arritmia sinusal respiratoria leve. Sin bloqueos. PR 0.12 s.','Hallazgo no patologico. Asociado a la cardiopatia subyacente.',65000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (15,'77234567',8,TIMESTAMP '2026-05-08 09:30:00','Biopsia histopatologica','URGENTE',
    'Lipoma maduro benigno. Margenes libres de celulas neoplasicas.','Sin tratamiento adicional necesario. Alta definitiva.',150000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (17,'77456789',9,TIMESTAMP '2026-05-12 11:00:00','Raspado cutaneo','NORMAL',
    'Negativo para acaros Demodex y Sarcoptes. Sin dermatofitos.','Descarta parasitosis. Confirma etiologia alergica pura.',45000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (17,'77456789',9,TIMESTAMP '2026-05-12 11:15:00','Cultivo bacteriano','NORMAL',
    'Sin crecimiento bacteriano patogeno tras 72h de incubacion.','Descarta piodermia secundaria. Diagnostico: dermatitis atopica.',72000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (12,'77456789',13,TIMESTAMP '2026-05-25 10:30:00','Oftalmoscopia','NORMAL',
    'Conjuntiva hiperemia. Secrecion mucopurulenta OD. Cornea sin ulcera.','Conjuntivitis bacteriana superficial OD. Sin compromiso corneal.',55000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (13,'77123456',15,TIMESTAMP '2026-06-01 11:30:00','Hemograma + Quimica','NORMAL',
    NULL,'Muestra procesada. Resultado en 24h. Evaluar funcion hepatica y perfil lipidico.',85000);
INSERT INTO EXAMEN_LAB (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_examen,prioridad,resultado,observaciones,costo)
VALUES (11,'77345678',NULL,TIMESTAMP '2026-06-05 08:30:00','Rx torax urgente','URGENTE',
    NULL,'Radiografia tomada. En lectura por radiologo. Evaluar edema pulmonar agudo.',75000);
COMMIT;
PROMPT '[OK] Examenes (12: NORMAL y URGENTE, con y sin resultado pendiente).';

-- ============================================================
-- CIRUGIAS (6): Programada / En Curso / 3 Realizadas / 1 Cancelada
-- TRG_CIRUGIA_VALIDA_CONSULTA: si id_consulta != NULL verifica id_paciente coincide
-- ============================================================
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (9,'77234567',5,TIMESTAMP '2026-04-15 14:30:00',
    'Ovariohisterectomia (esterilizacion)',
    'General isoflurano',
    'Abordaje ventral. Ligadura pedicular bilateral. Cierre 3 planos. Sin incidentes.',
    'Exitosa. Sin complicaciones intraoperatorias. Recuperacion en sala.',
    90, 280000,'Realizada',TIMESTAMP '2026-04-15 14:35:00',TIMESTAMP '2026-04-15 16:05:00');
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (15,'77234567',8,TIMESTAMP '2026-05-08 09:30:00',
    'Extraccion tumor cutaneo + biopsia',
    'General isoflurano',
    'Escision eliptica masa 2cm flanco derecho. Margenes 1cm. Cierre simple. Biopsia enviada.',
    'Lipoma benigno confirmado. Margenes libres. Alta sin recidiva esperada.',
    75, 320000,'Realizada',TIMESTAMP '2026-05-08 09:35:00',TIMESTAMP '2026-05-08 10:50:00');
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (3,'77234567',NULL,TIMESTAMP '2026-04-28 10:00:00',
    'Profilaxis dental + 2 extracciones',
    'General sevoflurano',
    'Tartrectomia ultrasonica. Extraccion incisivos 301 y 401 movilidad grado III. Sutura reabsorbible.',
    'Exitosa. Higiene bucal recuperada. Dolor leve post-procedimiento controlado.',
    80, 195000,'Realizada',TIMESTAMP '2026-04-28 10:10:00',TIMESTAMP '2026-04-28 11:30:00');
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (9,'77234567',NULL,TIMESTAMP '2026-06-12 14:30:00',
    'Artroscopia exploracion cadera',
    'Epidural + sedacion profunda',
    'Artroscopia diagnostica cadera derecha. Consentimiento firmado. Analisis pre-quirurgicos OK.',
    'Pendiente de realizacion.',
    NULL, 250000,'Programada',NULL,NULL);
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (15,'77234567',NULL,TIMESTAMP '2026-06-05 08:00:00',
    'Extraccion quiste subcutaneo axila',
    'General isoflurano',
    'Quiste epidermico 1.5cm axila izquierda. Cirugia iniciada sin incidentes.',
    NULL,
    NULL, 200000,'En Curso',TIMESTAMP '2026-06-05 08:10:00',NULL);
INSERT INTO CIRUGIA (id_paciente,cedula_veterinario,id_consulta,fecha_hora,tipo_cirugia,anestesia,descripcion,resultado,duracion,costo,estado,hora_inicio,hora_fin)
VALUES (6,'77234567',NULL,TIMESTAMP '2026-05-20 09:00:00',
    'Profilaxis dental electiva',
    'General sevoflurano',
    'Cancelada en pre-anestesia: ALT 285 U/L (ref <88). Hepatopatia activa detectada. Reprogramar.',
    'Cancelada — hepatopatia descubierta en pre-anestesia. Tratar antes de reprogramar.',
    NULL, 150000,'Cancelada',NULL,NULL);
COMMIT;
PROMPT '[OK] Cirugias (6: 3 Realizadas, 1 Programada, 1 En Curso, 1 Cancelada).';

-- ============================================================
-- VACUNACIONES (21 inserts) — TRG_VACUNACION_STOCK: -1 dosis por INSERT
-- Vacunas vencidas (Rabia Felina id=10) NO pueden usarse: trigger lanza error -20204
-- APLICADA(16) | PENDIENTE(1, no confirmada) | PROGRAMADA(2, reserva stock)
-- Los 2 APLICADA con proxima_fecha en el pasado son boosters vencidos para prueba
-- ============================================================
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (1,'77123456',2,TIMESTAMP '2026-03-10 08:10:00',DATE '2027-03-10','Nobivac Rabia adulto. Sin reacciones adversas.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (2,'77456789',3,TIMESTAMP '2026-03-15 09:40:00',DATE '2027-03-15','Triple Felina: Herpesvirus Calicivirus Panleucopenia. Sin reacciones.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (4,'77123456',1,TIMESTAMP '2026-02-15 09:00:00',DATE '2027-02-15','Vanguard Plus 5 refuerzo anual. Bien tolerada.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (4,'77123456',2,TIMESTAMP '2026-02-15 09:05:00',DATE '2027-02-15','Nobivac Rabia: obligatoria anual. Sin incidencias.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (7,'77123456',3,TIMESTAMP '2026-04-10 11:10:00',DATE '2027-04-10','Triple Felina: primer refuerzo adulto (2 anos). Sin fiebre.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (7,'77123456',5,TIMESTAMP '2026-04-10 11:15:00',DATE '2027-04-10','Leucemia Felina: riesgo moderado (contacto exterior). Sin reacciones.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (9,'77123456',1,TIMESTAMP '2026-01-20 09:00:00',DATE '2027-01-20','Vanguard Plus 5 refuerzo anual Beagle. Sin problemas.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (9,'77123456',2,TIMESTAMP '2026-01-20 09:05:00',DATE '2027-01-20','Nobivac Rabia anual obligatoria. Sin reacciones.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (5,'77123456',1,TIMESTAMP '2026-05-20 08:10:00',DATE '2027-05-20','Vanguard Plus 5: Coco French Poodle. Refuerzo anual sin novedades.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (11,'77345678',2,TIMESTAMP '2026-03-10 09:00:00',DATE '2027-03-10','Nobivac Rabia: Simba. Aplicada bajo supervision cardiologica. Sin incidentes.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (8,'77123456',6,TIMESTAMP '2026-03-25 10:00:00',DATE '2027-03-25','Parvovirus: Bella Schnauzer. Refuerzo anual. Sin reacciones.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (8,'77123456',4,TIMESTAMP '2026-03-25 10:05:00',DATE '2027-03-25','Bordetella: Bella frecuenta parques. Sin reacciones locales.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (18,'77345678',6,TIMESTAMP '2026-02-01 10:00:00',DATE '2027-02-01','Parvovirus: Thor Husky. Bien tolerada. Sin hipersensibilidad.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (18,'77345678',7,TIMESTAMP '2026-02-01 10:05:00',DATE '2027-02-01','Moquillo: Thor. Refuerzo anual. Sin problemas.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (15,'77234567',8,TIMESTAMP '2026-04-01 09:00:00',DATE '2027-04-01','Giardia: Zeus Rottweiler con acceso a rios. Primera dosis. Sin reacciones.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (17,'77456789',5,TIMESTAMP '2026-03-20 11:00:00',DATE '2027-03-20','Leucemia Felina: Pelusa Maine Coon exterior supervisado. Sin reacciones.','APLICADA');
-- Boosters vencidos (proxima_fecha en el pasado = urgente renovacion)
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (14,'77123456',1,TIMESTAMP '2026-01-15 09:00:00',DATE '2026-01-15','BOOSTER VENCIDO 2026-01-15. Lola Chihuahua. Contactar propietario urgente.','APLICADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (16,'77123456',8,TIMESTAMP '2026-01-05 09:00:00',DATE '2026-01-05','BOOSTER VENCIDO 2026-01-05. Daisy Yorkshire. Riesgo en perros de paseo frecuente.','APLICADA');
-- Pendiente: registrada sin confirmar con propietario
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (13,'77123456',6,TIMESTAMP '2026-06-10 09:00:00',NULL,'Parvovirus: Bruno pendiente confirmacion. Propietario llama el lunes.','PENDIENTE');
-- Programadas: reserva de stock para citas futuras
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (19,'77123456',9,TIMESTAMP '2026-06-20 09:00:00',DATE '2027-06-20','Hexavalente: Bolt Dalmata cita programada 20-Jun. Stock reservado.','PROGRAMADA');
INSERT INTO VACUNACION (id_paciente,cedula_veterinario,id_vacuna,fecha_hora,proxima_fecha,observaciones,estado)
VALUES (4,'77123456',6,TIMESTAMP '2026-06-30 10:00:00',DATE '2027-06-30','Parvovirus: Max refuerzo programado fin de mes.','PROGRAMADA');
COMMIT;
PROMPT '[OK] Vacunaciones (21: 16 APLICADA, 2 booster vencido, 1 PENDIENTE, 2 PROGRAMADA).';

-- ============================================================
-- SERVICIOS ESTETICOS (12)
-- BANO: BASICO MEDICADO ANTIPULGAS HIDRATANTE
-- MOTILADA: HIGIENICO ESTETICO RAZA PERSONALIZADO
-- Estados: REALIZADO PROGRAMADO CANCELADO
-- ============================================================
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',5,'88123456',TIMESTAMP '2026-04-08 10:00:00',45000,'REALIZADO','French Poodle. Bano de rutina. Pelo en buen estado.','BASICO',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',17,'88345678',TIMESTAMP '2026-05-12 13:00:00',55000,'REALIZADO','Maine Coon. Bano medicado prescrito por veterinaria por dermatitis atopica.','MEDICADO',1,0);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',8,'88123456',TIMESTAMP '2026-05-17 11:00:00',50000,'REALIZADO','Schnauzer. Bano hidratante mensual. Sin incidencias.','HIDRATANTE',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',11,'88234567',TIMESTAMP '2026-05-21 09:30:00',70000,'REALIZADO','Pastor Aleman. Doble sesion por pelo denso. Sin pulgas activas.','BASICO',1,0);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',18,'88234567',TIMESTAMP '2026-06-20 14:00:00',60000,'PROGRAMADO','Husky Siberiano. Bano antipulgas preventivo temporada de verano.','ANTIPULGAS',1,0);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,tipo_bano,incluye_secado,incluye_perfume)
VALUES ('BANO',1,'88123456',TIMESTAMP '2026-04-20 09:00:00',55000,'CANCELADO','Labrador. Propietaria no se presento. Sin aviso previo. Penalizacion 30%.','ANTIPULGAS',1,0);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',5,'88345678',TIMESTAMP '2026-05-11 10:00:00',70000,'REALIZADO','French Poodle. Corte de raza clasico. Propietaria muy satisfecha.','RAZA','MEDIANO',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',16,'88123456',TIMESTAMP '2026-05-19 11:30:00',60000,'REALIZADO','Yorkshire. Corte higienico zona inguinal y patas. Muy cooperativa.','HIGIENICO','CORTO',1,0);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',2,'88123456',TIMESTAMP '2026-05-30 10:00:00',65000,'REALIZADO','Persa. Corte estetico para verano. Propietaria pidio look moderno.','ESTETICO','MEDIANO',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',20,'88345678',TIMESTAMP '2026-06-22 15:00:00',65000,'PROGRAMADO','Angora. Primer corte estetico. Propietaria quiere largo.','ESTETICO','LARGO',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',4,'88345678',TIMESTAMP '2026-06-25 10:00:00',80000,'PROGRAMADO','Golden Retriever. Corte personalizado segun fotos aportadas. Sesion larga.','PERSONALIZADO','LARGO',1,1);
INSERT INTO SERVICIO_ESTETICO (tipo_servicio,id_paciente,cedula_estilista,fecha_hora,precio,estado_servicio,observaciones,estilo_corte,largo_corte,incluye_unas,incluye_limpieza)
VALUES ('MOTILADA',12,'88234567',TIMESTAMP '2026-04-15 14:00:00',55000,'CANCELADO','Comun Europeo. Cancelado: gato agredio al estilista. Reprogramar con feromonas.','HIGIENICO','CORTO',1,0);
COMMIT;
PROMPT '[OK] Servicios esteticos (12: 6 BANO + 6 MOTILADA | REALIZADO PROGRAMADO CANCELADO).';

-- ============================================================
-- FACTURAS (15) + DETALLE_FACTURA
-- TRG_DETALLE_ACTUALIZA_FACTURA recalcula subtotal/total automaticamente.
-- TRG_DETALLE_STOCK_MEDICAMENTO descuenta stock cuando tipo_concepto=MEDICAMENTO.
-- F1-F8: PAGADAS (fechas pasadas) | F9-F13: PENDIENTES | F14-F15: ANULADAS
-- ============================================================
PROMPT '[OK] Insertando facturas...';

-- F1: Rocky / Maria Rodriguez — PAGADA EFECTIVO — control anual + parasitologia + desparasitante
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065812345',1,TIMESTAMP '2026-03-10 09:00:00',0,0,'PAGADA','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta veterinaria general control anual Rocky','CONSULTA',1,60000,60000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Coproparasitologico resultado negativo','EXAMEN',1,35000,35000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Drontal Plus desparasitacion interna dosis unica','MEDICAMENTO',1,12000,12000,4);
    COMMIT;
END;
/

-- F2: Luna / Maria Rodriguez — PAGADA TARJETA — otitis + cultivo
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065812345',2,TIMESTAMP '2026-03-15 10:30:00',0,0,'PAGADA','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta dermatologica otitis externa felina','CONSULTA',1,75000,75000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Cultivo otico Candida albicans','EXAMEN',1,45000,45000);
    COMMIT;
END;
/

-- F3: Max / Carlos Mendoza — PAGADA TRANSFERENCIA — gastro + labs + internacion + medicamento
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065823456',4,TIMESTAMP '2026-04-07 17:00:00',0,0,'PAGADA','TRANSFERENCIA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta veterinaria gastroenteritis aguda Max','CONSULTA',1,65000,65000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Hemograma completo leucocitosis','EXAMEN',1,55000,55000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Quimica sanguinea BUN y Creatinina','EXAMEN',1,68000,68000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Internacion 2 dias hidratacion IV y monitoreo','INTERNACION',2,85000,170000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Omeprazol 20mg gastroproteccion 5 dias','MEDICAMENTO',1,9500,9500,6);
    COMMIT;
END;
/

-- F4: Toby / Jorge Pinto — PAGADA TARJETA — cirugia + internacion postQx + consulta + vacuna
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065845678',9,TIMESTAMP '2026-04-17 12:00:00',0,0,'PAGADA','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Cirugia ovariohisterectomia Toby Beagle','CIRUGIA',1,280000,280000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Internacion post-quirurgica 1 dia observacion','INTERNACION',1,75000,75000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta pre-quirurgica y evaluacion ASA','CONSULTA',1,80000,80000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Nobivac Rabia vacuna anual','VACUNA',1,35000,35000);
    COMMIT;
END;
/

-- F5: Simba / Laura Gomez — PAGADA EFECTIVO — cardiologia + eco + ECG + medicamento
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065856789',11,TIMESTAMP '2026-04-20 11:00:00',0,0,'PAGADA','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta cardiologia ICC grado II Simba','CONSULTA',1,120000,120000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Ecocardiograma insuficiencia mitral severa','EXAMEN',1,180000,180000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Electrocardiograma arritmia sinusal leve','EXAMEN',1,65000,65000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Omeprazol 20mg gastroproteccion tratamiento','MEDICAMENTO',1,9500,9500,6);
    COMMIT;
END;
/

-- F6: Zeus / Valentina Diaz — PAGADA TRANSFERENCIA — cirugia + biopsia + consulta
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065878901',15,TIMESTAMP '2026-05-10 16:00:00',0,0,'PAGADA','TRANSFERENCIA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Cirugia extraccion tumor cutaneo flanco Zeus','CIRUGIA',1,320000,320000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Biopsia histopatologica lipoma benigno','EXAMEN',1,150000,150000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta quirurgica pre y post operatoria','CONSULTA',1,90000,90000);
    COMMIT;
END;
/

-- F7: Coco / Carlos Mendoza — PAGADA EFECTIVO — estetica bano + motilada
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065823456',5,TIMESTAMP '2026-05-11 11:30:00',0,0,'PAGADA','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Bano basico con secado y perfume Coco','ESTETICA',1,45000,45000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Motilada raza MEDIANO con unas y limpieza Coco','ESTETICA',1,70000,70000);
    COMMIT;
END;
/

-- F8: Pelusa / Andres Lopez — PAGADA TARJETA — dermato + labs + bano + Apoquel
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065889012',17,TIMESTAMP '2026-05-14 12:00:00',0,0,'PAGADA','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta dermatologica dermatitis atopica Pelusa','CONSULTA',1,110000,110000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Raspado cutaneo negativo para acaros','EXAMEN',1,45000,45000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Cultivo bacteriano sin crecimiento','EXAMEN',1,72000,72000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Bano medicado Malaseb con secado','ESTETICA',1,55000,55000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Apoquel 16mg control prurito alergico 30 dias','MEDICAMENTO',1,145000,145000,9);
    COMMIT;
END;
/

-- F9: Bruno / Sebastian Ramirez — PENDIENTE EFECTIVO — obesidad + internacion + antibiotico
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065867890',13,TIMESTAMP '2026-06-03 12:00:00',0,0,'PENDIENTE','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta medicina general obesidad grado II Bruno','CONSULTA',1,60000,60000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Internacion 2 dias dieta supervisada (2x65000)','INTERNACION',2,65000,130000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Amoxicilina 250mg profilaxis infeccion secundaria','MEDICAMENTO',1,18500,18500,1);
    COMMIT;
END;
/

-- F10: Daisy / Valentina Diaz — PENDIENTE TRANSFERENCIA — urgencia gastroenteritis + internacion abierta
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065878901',16,TIMESTAMP '2026-05-28 13:30:00',0,0,'PENDIENTE','TRANSFERENCIA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta urgencia gastroenteritis Daisy Yorkshire','CONSULTA',1,65000,65000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Internacion provisional 1 dia (en curso, pendiente alta)','INTERNACION',1,75000,75000);
    COMMIT;
END;
/

-- F11: Mia / Laura Gomez — PENDIENTE TARJETA — consulta walk-in + oftalmoscopia
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065856789',12,TIMESTAMP '2026-05-26 11:00:00',0,0,'PENDIENTE','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta urgencia conjuntivitis bacteriana Mia','CONSULTA',1,55000,55000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Oftalmoscopia confirmacion diagnostica','EXAMEN',1,55000,55000);
    COMMIT;
END;
/

-- F12: Kira / Maria Rodriguez — PENDIENTE EFECTIVO — esguince + motilada
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065812345',3,TIMESTAMP '2026-05-15 17:00:00',0,0,'PENDIENTE','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta medicina general esguince tarsal Kira','CONSULTA',1,75000,75000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Motilada estetico MEDIANO pelaje mantenimiento','ESTETICA',1,65000,65000);
    COMMIT;
END;
/

-- F13: Simba / Laura Gomez — PENDIENTE EFECTIVO — ICC en curso + Rx urgente
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065856789',11,TIMESTAMP '2026-06-05 09:00:00',0,0,'PENDIENTE','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Internacion ICC Simba provisional 1 dia (en curso, alta pendiente)','INTERNACION',1,95000,95000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Rx torax urgente lectura pendiente','EXAMEN',1,75000,75000);
    COMMIT;
END;
/

-- F14: ANULADA — Rocky / Maria — bano antipulgas cancelado facturado por error
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065812345',1,TIMESTAMP '2026-04-20 10:00:00',0,0,'PENDIENTE','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Bano antipulgas CANCELADO facturado por error','ESTETICA',1,55000,55000);
    PKG_FACTURACION.anular_factura(v_f);
END;
/

-- F15: ANULADA — Bruno / Sebastian — consulta duplicada, ya cobrada en F9
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065867890',13,TIMESTAMP '2026-05-05 14:00:00',0,0,'PENDIENTE','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta DUPLICADO anulada ya incluida en factura correcta','CONSULTA',1,60000,60000);
    PKG_FACTURACION.anular_factura(v_f);
END;
/

-- ============================================================
-- FACTURAS JUNIO 2026 (Jun 1-5) — 5 PAGADAS
-- ============================================================

-- F16: PAGADA — Bella (Schnauzer) / Ana Castro — Control anual + antiparasitario, Jun 1
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065834567',8,TIMESTAMP '2026-06-01 09:00:00',0,0,'PAGADA','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta medicina preventiva revision general anual Schnauzer','CONSULTA',1,65000,65000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Bravecto aplicacion antiparasitaria externa dosis 3 meses','MEDICAMENTO',1,85000,85000,3);
    COMMIT;
END;
/

-- F17: PAGADA — Milo (Siames) / Ana Castro — Infeccion respiratoria superior felina, Jun 2
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065834567',7,TIMESTAMP '2026-06-02 10:30:00',0,0,'PAGADA','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta infeccion respiratoria superior felina Siames','CONSULTA',1,70000,70000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Cultivo faringeo bacteriologico Bordetella bronchiseptica','EXAMEN',1,48000,48000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Amoxicilina tratamiento antibiotico 10 dias','MEDICAMENTO',1,18500,18500,1);
    COMMIT;
END;
/

-- F18: PAGADA — Daisy (Yorkshire) / Valentina Diaz — Dermatitis pruriginosa, Jun 3
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065878901',16,TIMESTAMP '2026-06-03 11:00:00',0,0,'PAGADA','TRANSFERENCIA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta dermatitis pruriginosa cronica Yorkshire Terrier','CONSULTA',1,70000,70000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Raspado cutaneo cultivo micotico identificacion agente causal','EXAMEN',1,55000,55000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Apoquel control prurito alergico cronico 30 comprimidos','MEDICAMENTO',1,145000,145000,9);
    COMMIT;
END;
/

-- F19: PAGADA — Thor (Husky Siberiano) / Andres Lopez — Control ortopedico displasia, Jun 4
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065889012',18,TIMESTAMP '2026-06-04 14:00:00',0,0,'PAGADA','TARJETA') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Consulta control ortopedico displasia cadera Husky Siberiano','CONSULTA',1,80000,80000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Radiografia bilateral caderas proyeccion Penn-HIP evaluacion','EXAMEN',1,120000,120000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Meloxicam antiinflamatorio articular mantenimiento mensual','MEDICAMENTO',1,22000,22000,2);
    COMMIT;
END;
/

-- F20: PAGADA — Perla (Angora) / Natalia Vargas — Primera consulta + microchip + desparasitacion, Jun 5
DECLARE v_f NUMBER;
BEGIN
    INSERT INTO FACTURA (cedula_propietario,id_paciente,fecha_hora,subtotal,total,estado_factura,metodo_pago)
    VALUES ('1065900001',20,TIMESTAMP '2026-06-05 10:00:00',0,0,'PAGADA','EFECTIVO') RETURNING id INTO v_f;
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Primera consulta paciente nuevo Angora 10 meses apertura historia clinica','CONSULTA',1,65000,65000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal)
    VALUES (v_f,'Colocacion microchip ISO 11784 certificado SINCHI registro nacional','EXAMEN',1,45000,45000);
    INSERT INTO DETALLE_FACTURA (id_factura,descripcion,tipo_concepto,cantidad,precio_unitario,subtotal,id_medicamento)
    VALUES (v_f,'Drontal Plus desparasitacion interna profilaxis primer visita','MEDICAMENTO',1,12000,12000,4);
    COMMIT;
END;
/

COMMIT;
PROMPT '[OK] Facturas (20: 13 PAGADA / 5 PENDIENTE / 2 ANULADA).';
PROMPT '==========================================================';
PROMPT '  VetCare DDL Final v3.0 - Instalacion completada.';
PROMPT '  Resumen de datos cargados:';
PROMPT '    10 propietarios      |  20 pacientes';
PROMPT '    5 veterinarios       |  3 estilistas';
PROMPT '    14 medicamentos      |  10 vacunas';
PROMPT '    9 solicitudes        |  25 citas';
PROMPT '    15 consultas         |  5 internaciones';
PROMPT '    12 examenes lab      |  6 cirugias';
PROMPT '    21 vacunaciones      |  12 servicios esteticos';
PROMPT '    20 facturas (13 PAGADA + 5 PENDIENTE + 2 ANULADA)';
PROMPT '==========================================================';
