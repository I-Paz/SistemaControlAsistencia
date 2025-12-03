-- 1. Crear la Base de Datos
CREATE DATABASE ControlAsistenciasDB;
GO

-- 2. Seleccionar la Base de Datos para usarla
USE ControlAsistenciasDB;
GO

-- 3. Crear la Tabla de Empleados
-- Se usa INT para el ID (Matrícula) y BIT para el estado Activo (1=Sí, 0=No)
CREATE TABLE Empleado (
    IDEmpleado INT PRIMARY KEY,
    NombreEmpleado VARCHAR(50) NOT NULL,
    Puesto VARCHAR(50),
    Activo BIT DEFAULT 1,
    Contrasena VARCHAR(255) -- Solo necesaria para Administradores
);
GO

-- 4. Crear la Tabla de Registros de Asistencia
-- Se relaciona con Empleado mediante IDEmpleado
CREATE TABLE AsistenciaRegistro (
    IDRegistro INT IDENTITY(1,1) PRIMARY KEY,
    IDEmpleado INT,
    FechaHora DATETIME DEFAULT GETDATE(), -- Guarda la fecha y hora actual automáticamente
    TipoRegistro VARCHAR(50), -- 'Entrada' o 'Salida'
    FOREIGN KEY (IDEmpleado) REFERENCES Empleado(IDEmpleado)
);
GO

-- 5. Insertar el Administrador Inicial
-- Usuario: 100, Contraseña: admin123
INSERT INTO Empleado (IDEmpleado, NombreEmpleado, Puesto, Activo, Contrasena)
VALUES (100, 'Admin Principal', 'Gerente', 1, 'admin123');
GO