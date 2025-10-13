-- Actualizar tabla bootcamps para quitar capacidades_ids y usar nombres en inglés
ALTER TABLE bootcamps 
DROP COLUMN capacidades_ids,
CHANGE COLUMN nombre name VARCHAR(255) NOT NULL,
CHANGE COLUMN descripcion description TEXT,
CHANGE COLUMN fecha_lanzamiento launch_date DATE NOT NULL,
CHANGE COLUMN duracion duration INT NOT NULL,
MODIFY COLUMN id VARCHAR(36);

-- Recrear índices con nuevos nombres
DROP INDEX idx_bootcamps_nombre ON bootcamps;
DROP INDEX idx_bootcamps_fecha_lanzamiento ON bootcamps;
CREATE INDEX idx_bootcamps_name ON bootcamps(name);
CREATE INDEX idx_bootcamps_launch_date ON bootcamps(launch_date);