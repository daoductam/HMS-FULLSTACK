package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.dto.MedicineDTO;
import com.hms.PharmacyMS.dto.ResponseDTO;
import com.hms.PharmacyMS.service.MedicineService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path("/pharmacy/medicines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class MedicineAPI {
    private final MedicineService medicineService;

    @POST
    @Path("/add")
    public Response addMedicine(MedicineDTO medicineDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(medicineService.addMedicine(medicineDTO))
                .build();
    }

    @GET
    @Path("/get/{id}")
    public Response getMedicineById(@PathParam("id") Long id) {
        return Response.ok(medicineService.getMedicineById(id)).build();
    }

    @PUT
    @Path("/update")
    public Response updateMedicine(MedicineDTO medicineDTO) {
        medicineService.updateMedicine(medicineDTO);
        return Response.ok(new ResponseDTO("Medicine Updated")).build();
    }

    @GET
    @Path("/getAll")
    public Response getAllMedicines() {
        return Response.ok(medicineService.getAllMedicines()).build();
    }
}

