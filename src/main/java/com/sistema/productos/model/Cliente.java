package com.sistema.productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cliente", nullable=false)
    private Long id;

    @Size(max = 100, message = "El nombre debe tener máximo 100 caracteres")
    @Column(name="cli_nombre")
    private String nombre;

    @Size(max=100, message="El Apellido Paterno debe tener máximo 100 caracteres")
    @Column(name="cli_apellido_paterno")
    private String apellidoPaterno;

    @Size(max=100, message="El Apellido Materno debe tener máximo 100 caracteres")
    @Column(name="cli_apellido_materno")
    private String apellidoMaterno;

    @Size(max=100, message="La Dirección Personal debe tener máximo 100 caracteres")
    @Column(name="cli_direccion")
    private String direccion;

    @Size(max = 100, message = "El nombre de empresa debe tener máximo 100 caracteres")
    @Column(name="cli_nombre_empresa")
    private String nombreEmpresa;

    @Size(max = 100, message = "La direccion de empresa debe tener máximo 100 caracteres")
    @Column(name="cli_direccion_empresa")
    private String direccionEmpresa;

    @NotBlank(message="El Número de Documento es obligatorio")
    @Size(min=8, max=20, message="El Número De Documento debe tener entre 8 y 20 Caracteres")
    @Column(name="cli_ndocumento",nullable=false, unique = true)
    private String ndocumento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipodocumento", nullable = false)
    private TipoDocumento tipodocumento;

    @Email(message = "El correo debe tener un formato válido si se ingresa")
    @Column(name="cli_correo", unique = true, length=60)
    private String correo;

    @Pattern(regexp = "^$|^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos o estar vacío")
    @Column(name="cli_telefono", columnDefinition="char(9)", unique = true)
    private String telefono;

    @Column(name="cli_estado", nullable = false)
    private Integer estado = 1;

    public Cliente() {
    }

    public Cliente(String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String telefono, String direccion, String ndocumento, TipoDocumento tipodocumento, String nombreEmpresa, String direccionEmpresa) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = 1;
        this.ndocumento = ndocumento;
        this.tipodocumento = tipodocumento;
        this.nombreEmpresa = nombreEmpresa;
        this.direccionEmpresa = direccionEmpresa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDireccionEmpresa() {
        return direccionEmpresa;
    }

    public void setDireccionEmpresa(String direccionEmpresa) {
        this.direccionEmpresa = direccionEmpresa;
    }

    public String getNdocumento() {
        return ndocumento;
    }

    public void setNdocumento(String ndocumento) {
        this.ndocumento = ndocumento;
    }

    public TipoDocumento getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(TipoDocumento tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}
