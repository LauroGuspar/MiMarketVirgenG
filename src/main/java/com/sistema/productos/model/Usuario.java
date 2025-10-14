package com.sistema.productos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "empleado")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_empleado", nullable=false)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name="emple_nombre",nullable = false)
    private String nombre;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Column(name="emple_nombreuser",nullable = false, unique = true, length = 50)
    private String usuario;

    @NotBlank(message="El Apellido Paterno es obligatorio")
    @Size(min=5, max=100, message="El Apellido Paterno debe de tener mínimos 5 y maximo 100 caracteres")
    @Column(name="emple_apellido_paterno",nullable=false)
    private String apellidoPaterno;

    @NotBlank(message="El Apellido Materno es obligatorio")
    @Size(min=5, max=100, message="El Apellido Materno debe de tener mínimos 5 y maximo 100 caracteres")
    @Column(name="emple_apellido_materno", nullable=false)
    private String apellidoMaterno;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Column(name="emple_correo", nullable = false, unique = true, length=60)
    private String correo;

    @Size(min = 6, max=150, message = "La clave debe tener al menos 6 caracteres")
    @Column(name="emple_contrasena",nullable = false)
    private String clave;

    @NotBlank(message="El Telefono es obligatorio")
    @Size(min=9, max=9, message="El Número telefonico debe de tener mínimos 9 y maximo 9 caracteres")
    @Column(name="emple_telefono", nullable=false, unique = true)
    private String telefono;

    @NotBlank(message="La dirección es obligatorio")
    @Size(min=10, max=100, message="La Dirección debe de tener mínimos 20 y maximo 100 caracteres")
    @Column(name="emple_direccion",nullable=false)
    private String direccion;
    
    @Column(name="emple_estado", nullable = false)
    private Integer estado = 1;

    @NotBlank(message="El Número de Documento es obligatorio")
    @Size(min=8, max=20, message="El Número De Documento debe de tener mínimos 8 Caracteres")
    @Column(name="emple_ndocumento",nullable=false, unique = true)
    private String ndocumento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipodocumento")
    private TipoDocumento tipodocumento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    // Constructor por defecto
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String usuario,String apellidoPaterno,String apellidoMaterno,String correo, String clave,String telefono, String direccion,String ndocumento,TipoDocumento tipodocumento, Rol rol) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.clave = clave;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = 1;
        this.ndocumento = ndocumento;
        this.tipodocumento = tipodocumento;
        this.rol= rol;
    }

    // Getters y Setters
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}