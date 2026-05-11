package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.clients.PaymentClient;
import com.hms.PharmacyMS.dto.CreateSaleResponse;
import com.hms.PharmacyMS.dto.ResponseDTO;
import com.hms.PharmacyMS.dto.SaleDTO;
import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.dto.SaleRequest;
import com.hms.PharmacyMS.service.SaleItemService;
import com.hms.PharmacyMS.service.SaleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/pharmacy/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SaleAPI {
    private final SaleService saleService;
    private final SaleItemService saleItemService;
    
    @Inject
    @RestClient
    PaymentClient paymentClient;

    @POST
    @Path("/create")
    public Response createSale(SaleRequest dto) {
        // 1. Tạo sale (status sẽ là PAID nếu DIRECT, PENDING nếu MOMO)
        Long saleId = saleService.createSale(dto);
        
        // 2. Nếu là thanh toán trực tiếp, không cần tạo payment link
        if ("DIRECT".equalsIgnoreCase(dto.getPaymentMethod())) {
            CreateSaleResponse response = CreateSaleResponse.builder()
                    .saleId(saleId)
                    .paymentUrl(null)
                    .build();
            return Response.status(Response.Status.CREATED).entity(response).build();
        }
        
        // 3. Nếu là MOMO, gọi PaymentMS để tạo payment link
        String orderId = "SALE-" + saleId;
        String paymentUrl = paymentClient.createMomoPayment(orderId, dto.getTotalAmount());
        
        // 4. Trả về saleId và paymentUrl
        CreateSaleResponse response = CreateSaleResponse.builder()
                .saleId(saleId)
                .paymentUrl(paymentUrl)
                .build();
        
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/update")
    public Response updateSale(SaleDTO dto) {
        saleService.updateSale(dto);
        return Response.ok(new ResponseDTO("Sale updated successfull")).build();
    }

    @GET
    @Path("/getSaleItems/{saleId}")
    public Response getSaleItems(@PathParam("saleId") Long saleId) {
        List<SaleItemDTO> saleItems = saleItemService.getSaleItemBySaleId(saleId);
        return Response.ok(saleItems).build();
    }

    @GET
    @Path("/get/{id}")
    public Response getSale(@PathParam("id") Long id) {
        SaleDTO sale = saleService.getSale(id);
        return Response.ok(sale).build();
    }

    @GET
    @Path("/getAll")
    public Response getAllSales() {
        List<SaleDTO> sales = saleService.getAllSales();
        return Response.ok(sales).build();
    }
}

