package com.vd.payments.XDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.vd.payments.MODELO.enums.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @ApiModel
public class OperadorSaveDTO extends BaseDTO
{
    @NotBlank(message = "Password cannot be empty")
    @Min(value = 4 , message = "Password length cannot be less than 4 characters")
    private String pass;
    @NotBlank(message = "Nombre cannot be empty")
    private String nombre;
    @NotBlank(message = "Nombre cannot be empty")
    private String apellido;
    @NotBlank(message = "Nombre cannot be empty")
    @Email(message = "Need to introduce a valid email")
    private String email;
//    private String dni;
    @ApiModelProperty
    private Sexo sexo;
//    private int fkInstalacion;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "dd/MM/yyyy", example = "20/02/2022")
    private LocalDate fechaNacimiento;

}
