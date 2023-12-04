package com.vd.payments.XDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.vd.payments.MODELO.enums.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @ApiModel
public class OperadorSaveDTO extends BaseDTO
{
    private String pass;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    @ApiModelProperty
    private Sexo sexo;
    private int fkInstalacion;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "dd/MM/yyyy", example = "20/02/2022")
    private LocalDate fechaNacimiento;

}
