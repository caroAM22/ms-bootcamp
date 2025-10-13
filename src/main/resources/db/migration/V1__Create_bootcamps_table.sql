CREATE TABLE bootcamps (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_lanzamiento DATE NOT NULL,
    duracion INT NOT NULL,
    capacidades_ids JSON NOT NULL
);

CREATE INDEX idx_bootcamps_nombre ON bootcamps(nombre);
CREATE INDEX idx_bootcamps_fecha_lanzamiento ON bootcamps(fecha_lanzamiento);