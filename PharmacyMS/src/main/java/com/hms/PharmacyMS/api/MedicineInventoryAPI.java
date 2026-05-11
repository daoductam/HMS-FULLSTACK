package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.dto.MedicineInventoryDTO;
import com.hms.PharmacyMS.service.MedicineInventoryService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/pharmacy/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class MedicineInventoryAPI {
    private final MedicineInventoryService medicineInventoryService;

    @POST
    @Path("/add")
    public Response addMedicine(MedicineInventoryDTO medicineDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(medicineInventoryService.addMedicine(medicineDTO))
                .build();
    }

    @GET
    @Path("/get/{id}")
    public Response getMedicineById(@PathParam("id") Long id) {
        return Response.ok(medicineInventoryService.getMedicineById(id)).build();
    }

    @PUT
    @Path("/update")
    public Response updateMedicine(MedicineInventoryDTO medicineDTO) {
        return Response.ok(medicineInventoryService.updateMedicine(medicineDTO)).build();
    }

    @GET
    @Path("/getAll")
    public Response getAllMedicines() {
        return Response.ok(medicineInventoryService.getAllMedicines()).build();
    }
}

