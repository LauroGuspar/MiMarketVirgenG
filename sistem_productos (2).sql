-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-10-2025 a las 20:04:01
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sistem_productos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE `categoria` (
  `id_categoria` bigint(20) NOT NULL,
  `categ_nombre` varchar(100) NOT NULL,
  `categ_imagen` varchar(255) DEFAULT NULL,
  `categ_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`id_categoria`, `categ_nombre`, `categ_imagen`, `categ_estado`) VALUES
(1, 'Lácteos', NULL, 1),
(2, 'Bebidas', NULL, 1),
(3, 'Limpieza', NULL, 1),
(4, 'Alimentos ', NULL, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_tipo_producto`
--

CREATE TABLE `categoria_tipo_producto` (
  `id_categoria` bigint(20) NOT NULL,
  `id_tipoproducto` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `id_cliente` bigint(20) NOT NULL,
  `cli_nombre` varchar(100) DEFAULT NULL,
  `cli_apellido_paterno` varchar(100) DEFAULT NULL,
  `cli_apellido_materno` varchar(100) DEFAULT NULL,
  `cli_correo` varchar(60) DEFAULT NULL,
  `cli_telefono` char(9) DEFAULT NULL,
  `cli_estado` int(11) NOT NULL DEFAULT 1,
  `cli_direccion` varchar(100) NOT NULL,
  `cli_ndocumento` varchar(20) NOT NULL,
  `cli_nombre_empresa` varchar(100) DEFAULT NULL,
  `cli_direccion_empresa` varchar(100) DEFAULT NULL,
  `id_tipodocumento` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `compania_transporte`
--

CREATE TABLE `compania_transporte` (
  `id_transporte` bigint(20) NOT NULL,
  `transport_nombre` varchar(100) NOT NULL,
  `transport_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `contacto`
--

CREATE TABLE `contacto` (
  `id_contacto` bigint(20) NOT NULL,
  `contac_nombre` varchar(100) NOT NULL,
  `contac_apellido_paterno` varchar(100) NOT NULL,
  `contac_apellido_materno` varchar(100) NOT NULL,
  `contac_telefono` char(9) NOT NULL,
  `contac_observaciones` varchar(300) DEFAULT NULL,
  `id_proveedor` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_orden`
--

CREATE TABLE `detalle_orden` (
  `id_producto` bigint(20) NOT NULL,
  `id_orden` bigint(20) NOT NULL,
  `detalleorden_precio_original` decimal(10,0) NOT NULL,
  `detalleorden_cantidad` int(11) NOT NULL,
  `detalleorden_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_venta`
--

CREATE TABLE `detalle_venta` (
  `id_detalle` bigint(20) NOT NULL,
  `detal_venta_cantidad` int(11) NOT NULL,
  `detal_venta_precio` decimal(10,0) DEFAULT 0,
  `detal_venta_subtotal` decimal(10,0) DEFAULT 0,
  `id_venta` bigint(20) NOT NULL,
  `id_producto` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `direccion_entrega`
--

CREATE TABLE `direccion_entrega` (
  `id_entrega` bigint(20) NOT NULL,
  `entregadirec_mombre_cliente` varchar(100) NOT NULL,
  `entregadirec_telefono` char(9) NOT NULL,
  `entregadirec_provincia` varchar(100) NOT NULL,
  `entregadirec_direccion` varchar(150) NOT NULL,
  `id_cliente` bigint(20) NOT NULL,
  `id_venta` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `id_empleado` bigint(20) NOT NULL,
  `emple_nombre` varchar(100) NOT NULL,
  `emple_nombreuser` varchar(50) NOT NULL,
  `emple_apellido_paterno` varchar(100) NOT NULL,
  `emple_apellido_materno` varchar(100) NOT NULL,
  `emple_correo` varchar(60) NOT NULL,
  `emple_contrasena` varchar(150) NOT NULL,
  `emple_telefono` varchar(9) NOT NULL,
  `emple_direccion` varchar(100) NOT NULL,
  `emple_estado` int(11) NOT NULL DEFAULT 1,
  `emple_ndocumento` varchar(20) NOT NULL,
  `id_tipodocumento` bigint(20) NOT NULL,
  `id_rol` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empleado`
--

INSERT INTO `empleado` (`id_empleado`, `emple_nombre`, `emple_nombreuser`, `emple_apellido_paterno`, `emple_apellido_materno`, `emple_correo`, `emple_contrasena`, `emple_telefono`, `emple_direccion`, `emple_estado`, `emple_ndocumento`, `id_tipodocumento`, `id_rol`) VALUES
(1, 'Roger', 'admin', 'Velasco', 'Zapata', 'roger.1710@gmail.com', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', '945027855', 'Direccion Ficticia', 1, '72934888', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `forma_pago`
--

CREATE TABLE `forma_pago` (
  `id_fpago` bigint(20) NOT NULL,
  `fpago_metodo` varchar(15) DEFAULT NULL,
  `fpago_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `marca`
--

CREATE TABLE `marca` (
  `id_marca` bigint(20) NOT NULL,
  `marca_nombre` varchar(100) NOT NULL,
  `marca_fecha` date DEFAULT current_timestamp(),
  `marca_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `marca`
--

INSERT INTO `marca` (`id_marca`, `marca_nombre`, `marca_fecha`, `marca_estado`) VALUES
(1, 'Marca01', '2025-10-13', 1),
(2, 'Marca02', '2025-10-13', 1),
(3, 'Marca03', '2025-10-13', 1),
(4, 'Marca04', '2025-10-13', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `opcion`
--

CREATE TABLE `opcion` (
  `id_opcion` bigint(20) NOT NULL,
  `opcion_nombre` varchar(100) NOT NULL,
  `opcion_ruta` varchar(100) NOT NULL,
  `opcion_icon` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `opcion`
--

INSERT INTO `opcion` (`id_opcion`, `opcion_nombre`, `opcion_ruta`, `opcion_icon`) VALUES
(1, 'Dashboard', '/', NULL),
(2, 'Empleados', '/empleados/listar', NULL),
(3, 'Roles', '/empleados/roles/listar', NULL),
(4, 'Clientes', '/clientes/listar', NULL),
(5, 'Productos', '/productos/listar', NULL),
(6, 'Categorías', '/productos/categorias/listar', NULL),
(7, 'Marcas', '/productos/marcas/listar', NULL),
(8, 'Unidad', '/productos/unidad/listar', NULL),
(9, 'Tipo de Productos', '/productos/tipos-producto/listar', NULL),
(10, 'Compras', '/compras//listar', NULL),
(11, 'Proovedores', '/compras/proovedor/listar', NULL),
(12, 'Ordenes de Compra', '/compras/ordenes/listar', NULL),
(13, 'Listado de Ventas', '/ventas/listar', NULL),
(14, 'Nuevo Comprobante', '/ventas/comprobante/nuevo', NULL),
(15, 'Punto de Venta', '/ventas/punto-venta/nuevo', NULL),
(16, 'Movimientos', '/inventarios/movimientos/listar', NULL),
(17, 'Traslados', '/inventarios/traslados/listar', NULL),
(18, 'Devolucion A Proovedor', '/inventarios/devolucion/listar', NULL),
(19, 'Pedidos', '/tiendavirtual/pedidos/listar', NULL),
(20, 'Productos Tienda V.', '/tiendavirtual/productos/listar', NULL),
(21, 'Tags-Categorias', '/tiendavirtual/tags/listar', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `orden`
--

CREATE TABLE `orden` (
  `id_orden` bigint(20) NOT NULL,
  `order_fecha` date NOT NULL,
  `order_fecha_requerida` date NOT NULL,
  `order_fecha_envio` date DEFAULT NULL,
  `order_nombre_transportista` varchar(150) NOT NULL,
  `order_direccion_envio` varchar(250) NOT NULL,
  `order_ciudad` varchar(100) NOT NULL,
  `order_provincia` varchar(100) NOT NULL,
  `id_transporte` bigint(20) NOT NULL,
  `id_proveedor` bigint(20) NOT NULL,
  `id_empleado` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` bigint(20) NOT NULL,
  `produc_nombre` varchar(100) NOT NULL,
  `produc_codigo` varchar(50) NOT NULL,
  `produc_descripcion` varchar(150) NOT NULL,
  `produc_precio` decimal(10,0) NOT NULL,
  `produc_fecha_creacion` date DEFAULT NULL,
  `produc_fecha_vencimiento` date DEFAULT NULL,
  `produc_stock` int(11) DEFAULT 0,
  `produc_stock_minimo` int(11) DEFAULT 1,
  `produc_imagen` varchar(255) DEFAULT NULL,
  `produc_estado` int(11) NOT NULL DEFAULT 1,
  `id_unidad` int(11) NOT NULL,
  `id_categoria` bigint(20) NOT NULL,
  `id_marca` bigint(20) NOT NULL,
  `id_tipoproducto` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedor`
--

CREATE TABLE `proveedor` (
  `id_proveedor` bigint(20) NOT NULL,
  `provee_nombre` varchar(100) NOT NULL,
  `provee_nombre_comercial` varchar(100) NOT NULL,
  `provee_nacionalidad` varchar(100) NOT NULL,
  `provee_direccion` varchar(100) NOT NULL,
  `provee_telefono` char(9) NOT NULL,
  `provee_correo` varchar(150) NOT NULL,
  `provee_correo_adicional` varchar(150) DEFAULT NULL,
  `provee_estado` int(11) NOT NULL DEFAULT 1,
  `provee_ndocumento` varchar(20) NOT NULL,
  `id_tipodocumento` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `id_rol` bigint(20) NOT NULL,
  `rol_nombre` varchar(50) NOT NULL,
  `rol_descripcion` varchar(255) DEFAULT NULL,
  `rol_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_rol`, `rol_nombre`, `rol_descripcion`, `rol_estado`) VALUES
(1, 'Administrador', 'Acceso total al sistema.', 1),
(2, 'Editor', 'Puede gestionar usuarios pero no Roles.', 1),
(3, 'Supervisor', 'Solo puede visualizar información.', 1),
(4, 'Empleado', '', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol_opcion`
--

CREATE TABLE `rol_opcion` (
  `id_rol` bigint(20) NOT NULL,
  `id_opcion` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol_opcion`
--

INSERT INTO `rol_opcion` (`id_rol`, `id_opcion`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(2, 1),
(2, 2),
(3, 1),
(4, 13),
(4, 14),
(4, 15),
(4, 19);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_documento`
--

CREATE TABLE `tipo_documento` (
  `id_tipodocumento` bigint(20) NOT NULL,
  `tipodoc_nombre` varchar(50) NOT NULL,
  `tipodoc_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tipo_documento`
--

INSERT INTO `tipo_documento` (`id_tipodocumento`, `tipodoc_nombre`, `tipodoc_estado`) VALUES
(1, 'DNI', 1),
(2, 'RUC', 1),
(3, 'Carné de Extranjería', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_producto`
--

CREATE TABLE `tipo_producto` (
  `id_tipoproducto` bigint(20) NOT NULL,
  `tipoproducto_nombre` varchar(50) NOT NULL,
  `tipoproducto_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_venta`
--

CREATE TABLE `tipo_venta` (
  `id_tipoventa` bigint(20) NOT NULL,
  `tipoventa_nombre` varchar(50) NOT NULL,
  `tipoventa_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `unidad`
--

CREATE TABLE `unidad` (
  `id_unidad` int(11) NOT NULL,
  `uni_nombre` varchar(50) NOT NULL,
  `uni_estado` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `unidad`
--

INSERT INTO `unidad` (`id_unidad`, `uni_nombre`, `uni_estado`) VALUES
(1, 'Unidades', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `venta`
--

CREATE TABLE `venta` (
  `id_venta` bigint(20) NOT NULL,
  `venta_fecha` date NOT NULL,
  `venta_subtotal` decimal(10,0) DEFAULT 0,
  `venta_igv` decimal(10,0) DEFAULT 0,
  `venta_total` decimal(10,0) DEFAULT 0,
  `id_cliente` bigint(20) NOT NULL,
  `id_fpago` bigint(20) NOT NULL,
  `id_empleado` bigint(20) NOT NULL,
  `id_tipoventa` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `categ_nombre` (`categ_nombre`);

--
-- Indices de la tabla `categoria_tipo_producto`
--
ALTER TABLE `categoria_tipo_producto`
  ADD PRIMARY KEY (`id_categoria`,`id_tipoproducto`),
  ADD KEY `FK_TipoProduc_CategoriaTipoProduc` (`id_tipoproducto`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`),
  ADD UNIQUE KEY `cli_correo` (`cli_correo`),
  ADD KEY `FK_TipoDoc_Cliente` (`id_tipodocumento`);

--
-- Indices de la tabla `compania_transporte`
--
ALTER TABLE `compania_transporte`
  ADD PRIMARY KEY (`id_transporte`),
  ADD UNIQUE KEY `transport_nombre` (`transport_nombre`);

--
-- Indices de la tabla `contacto`
--
ALTER TABLE `contacto`
  ADD PRIMARY KEY (`id_contacto`),
  ADD KEY `FK_Proveedor_Contacto` (`id_proveedor`);

--
-- Indices de la tabla `detalle_orden`
--
ALTER TABLE `detalle_orden`
  ADD PRIMARY KEY (`id_producto`,`id_orden`),
  ADD KEY `FK_Orden_DetalleOrden` (`id_orden`);

--
-- Indices de la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `FK_Venta_DetalletVenta` (`id_venta`),
  ADD KEY `FK_Produc_DetalletVenta` (`id_producto`);

--
-- Indices de la tabla `direccion_entrega`
--
ALTER TABLE `direccion_entrega`
  ADD PRIMARY KEY (`id_entrega`),
  ADD KEY `FK_Cliente_Entrega` (`id_cliente`),
  ADD KEY `FK_Venta_Entrega` (`id_venta`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`id_empleado`),
  ADD UNIQUE KEY `emple_nombreuser` (`emple_nombreuser`),
  ADD UNIQUE KEY `emple_correo` (`emple_correo`),
  ADD UNIQUE KEY `emple_telefono` (`emple_telefono`),
  ADD UNIQUE KEY `emple_ndocumento` (`emple_ndocumento`),
  ADD KEY `FK_TipoDoc_Empleado` (`id_tipodocumento`),
  ADD KEY `FK_Rol_Empleado` (`id_rol`);

--
-- Indices de la tabla `forma_pago`
--
ALTER TABLE `forma_pago`
  ADD PRIMARY KEY (`id_fpago`),
  ADD UNIQUE KEY `fpago_metodo` (`fpago_metodo`);

--
-- Indices de la tabla `marca`
--
ALTER TABLE `marca`
  ADD PRIMARY KEY (`id_marca`);

--
-- Indices de la tabla `opcion`
--
ALTER TABLE `opcion`
  ADD PRIMARY KEY (`id_opcion`),
  ADD UNIQUE KEY `opcion_ruta` (`opcion_ruta`);

--
-- Indices de la tabla `orden`
--
ALTER TABLE `orden`
  ADD PRIMARY KEY (`id_orden`),
  ADD KEY `FK_Transporte_Orden` (`id_transporte`),
  ADD KEY `FK_Proveedor_Orden` (`id_proveedor`),
  ADD KEY `FK_Empleado_Orden` (`id_empleado`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`),
  ADD KEY `FK_Unidad_Produc` (`id_unidad`),
  ADD KEY `FK_Catego_Produc` (`id_categoria`),
  ADD KEY `FK_Marca_Produc` (`id_marca`),
  ADD KEY `FK_TipoProduc_Produc` (`id_tipoproducto`);

--
-- Indices de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id_proveedor`),
  ADD UNIQUE KEY `provee_nombre` (`provee_nombre`),
  ADD KEY `FK_TipoDoc_Proveedor` (`id_tipodocumento`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `rol_nombre` (`rol_nombre`);

--
-- Indices de la tabla `rol_opcion`
--
ALTER TABLE `rol_opcion`
  ADD PRIMARY KEY (`id_rol`,`id_opcion`),
  ADD KEY `FK_Opcion` (`id_opcion`);

--
-- Indices de la tabla `tipo_documento`
--
ALTER TABLE `tipo_documento`
  ADD PRIMARY KEY (`id_tipodocumento`),
  ADD UNIQUE KEY `tipodoc_nombre` (`tipodoc_nombre`);

--
-- Indices de la tabla `tipo_producto`
--
ALTER TABLE `tipo_producto`
  ADD PRIMARY KEY (`id_tipoproducto`),
  ADD UNIQUE KEY `tipoproducto_nombre` (`tipoproducto_nombre`);

--
-- Indices de la tabla `tipo_venta`
--
ALTER TABLE `tipo_venta`
  ADD PRIMARY KEY (`id_tipoventa`),
  ADD UNIQUE KEY `tipoventa_nombre` (`tipoventa_nombre`);

--
-- Indices de la tabla `unidad`
--
ALTER TABLE `unidad`
  ADD PRIMARY KEY (`id_unidad`),
  ADD UNIQUE KEY `uni_nombre` (`uni_nombre`);

--
-- Indices de la tabla `venta`
--
ALTER TABLE `venta`
  ADD PRIMARY KEY (`id_venta`),
  ADD KEY `FK_Cliente_Venta` (`id_cliente`),
  ADD KEY `FK_FPago_Venta` (`id_fpago`),
  ADD KEY `FK_Empleado_Venta` (`id_empleado`),
  ADD KEY `FK_TipoVenta_Venta` (`id_tipoventa`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria`
--
ALTER TABLE `categoria`
  MODIFY `id_categoria` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id_cliente` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `compania_transporte`
--
ALTER TABLE `compania_transporte`
  MODIFY `id_transporte` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `contacto`
--
ALTER TABLE `contacto`
  MODIFY `id_contacto` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  MODIFY `id_detalle` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `direccion_entrega`
--
ALTER TABLE `direccion_entrega`
  MODIFY `id_entrega` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `id_empleado` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `forma_pago`
--
ALTER TABLE `forma_pago`
  MODIFY `id_fpago` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `marca`
--
ALTER TABLE `marca`
  MODIFY `id_marca` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `opcion`
--
ALTER TABLE `opcion`
  MODIFY `id_opcion` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `orden`
--
ALTER TABLE `orden`
  MODIFY `id_orden` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id_proveedor` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `id_rol` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `tipo_documento`
--
ALTER TABLE `tipo_documento`
  MODIFY `id_tipodocumento` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tipo_producto`
--
ALTER TABLE `tipo_producto`
  MODIFY `id_tipoproducto` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tipo_venta`
--
ALTER TABLE `tipo_venta`
  MODIFY `id_tipoventa` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `unidad`
--
ALTER TABLE `unidad`
  MODIFY `id_unidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `venta`
--
ALTER TABLE `venta`
  MODIFY `id_venta` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `categoria_tipo_producto`
--
ALTER TABLE `categoria_tipo_producto`
  ADD CONSTRAINT `FK_Categoria_CategoriaTipoProduc` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`),
  ADD CONSTRAINT `FK_TipoProduc_CategoriaTipoProduc` FOREIGN KEY (`id_tipoproducto`) REFERENCES `tipo_producto` (`id_tipoproducto`);

--
-- Filtros para la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD CONSTRAINT `FK_TipoDoc_Cliente` FOREIGN KEY (`id_tipodocumento`) REFERENCES `tipo_documento` (`id_tipodocumento`);

--
-- Filtros para la tabla `contacto`
--
ALTER TABLE `contacto`
  ADD CONSTRAINT `FK_Proveedor_Contacto` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`);

--
-- Filtros para la tabla `detalle_orden`
--
ALTER TABLE `detalle_orden`
  ADD CONSTRAINT `FK_Orden_DetalleOrden` FOREIGN KEY (`id_orden`) REFERENCES `orden` (`id_orden`),
  ADD CONSTRAINT `FK_Producto_DetalleOrden` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`);

--
-- Filtros para la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD CONSTRAINT `FK_Produc_DetalletVenta` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  ADD CONSTRAINT `FK_Venta_DetalletVenta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`);

--
-- Filtros para la tabla `direccion_entrega`
--
ALTER TABLE `direccion_entrega`
  ADD CONSTRAINT `FK_Cliente_Entrega` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  ADD CONSTRAINT `FK_Venta_Entrega` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`);

--
-- Filtros para la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD CONSTRAINT `FK_Rol_Empleado` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`),
  ADD CONSTRAINT `FK_TipoDoc_Empleado` FOREIGN KEY (`id_tipodocumento`) REFERENCES `tipo_documento` (`id_tipodocumento`);

--
-- Filtros para la tabla `orden`
--
ALTER TABLE `orden`
  ADD CONSTRAINT `FK_Empleado_Orden` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  ADD CONSTRAINT `FK_Proveedor_Orden` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  ADD CONSTRAINT `FK_Transporte_Orden` FOREIGN KEY (`id_transporte`) REFERENCES `compania_transporte` (`id_transporte`);

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `FK_Catego_Produc` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`),
  ADD CONSTRAINT `FK_Marca_Produc` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id_marca`),
  ADD CONSTRAINT `FK_TipoProduc_Produc` FOREIGN KEY (`id_tipoproducto`) REFERENCES `tipo_producto` (`id_tipoproducto`),
  ADD CONSTRAINT `FK_Unidad_Produc` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`);

--
-- Filtros para la tabla `proveedor`
--
ALTER TABLE `proveedor`
  ADD CONSTRAINT `FK_TipoDoc_Proveedor` FOREIGN KEY (`id_tipodocumento`) REFERENCES `tipo_documento` (`id_tipodocumento`);

--
-- Filtros para la tabla `rol_opcion`
--
ALTER TABLE `rol_opcion`
  ADD CONSTRAINT `FK_Opcion` FOREIGN KEY (`id_opcion`) REFERENCES `opcion` (`id_opcion`),
  ADD CONSTRAINT `FK_Rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`);

--
-- Filtros para la tabla `venta`
--
ALTER TABLE `venta`
  ADD CONSTRAINT `FK_Cliente_Venta` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  ADD CONSTRAINT `FK_Empleado_Venta` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  ADD CONSTRAINT `FK_FPago_Venta` FOREIGN KEY (`id_fpago`) REFERENCES `forma_pago` (`id_fpago`),
  ADD CONSTRAINT `FK_TipoVenta_Venta` FOREIGN KEY (`id_tipoventa`) REFERENCES `tipo_venta` (`id_tipoventa`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
