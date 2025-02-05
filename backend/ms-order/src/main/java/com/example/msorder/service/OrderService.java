package com.example.msorder.service;

import com.example.msorder.dto.OrderResponseDTO;
import com.example.msorder.feign.EventDTOStock;
import com.example.msorder.feign.IEventFeign;
import com.example.msorder.model.Order;
import com.example.msorder.model.Ticket;
import com.example.msorder.repository.IOrderRepository;
import com.example.msorder.repository.IticketRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final IOrderRepository repository;
    private final IticketRepository ticketrepository;
    private final EmailService emailSenderService;

    @Autowired
    private final IEventFeign eventFeign;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Optional<Order> findById(Long id){
        return repository.findById(id);
    }

    public Order save(Order order) {
        Double tot  = 0.0;
        Ticket ticket = new Ticket();
        ticket.setEventid(ticketrepository.findById(order.getTicket().get(0).getID()).get().getEventid());
        for (Ticket tickets: order.getTicket()
             ) {
            Ticket tot2 = ticketrepository.findById(tickets.getID()).get();
            tot += tot2.getPrice();
        }
        order.setTotal_price(tot);

        Order o = repository.save(order);
        emailSenderService.simpleEmail(order.getEmail(),tot,o.getID(),order.getTicket().size(),ticket.getEventid());
        EventDTOStock eventDTOStock = new EventDTOStock(ticket.getEventid(),order.getTicket().size());
        eventFeign.updateStock(eventDTOStock);
        return o;
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public Order update(Order order) {
        return repository.save(order);
    }

    public List<Order> findByUserid(Long id){
        return repository.findByUserid(id);
    }

    public List<OrderResponseDTO> findOrderByUseridAndEvent(Long id){

        List<Order> orders = repository.findByUserid(id);

        List<OrderResponseDTO> ordersDTO = new ArrayList<>();

        if(!orders.isEmpty()){
            orders.stream().forEach(order -> {
                Long eventId = order.getTicket().get(0).getEventid();

                IEventFeign.EventDTO eventDTO = eventFeign.getEventById(eventId);
                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

                orderResponseDTO.setEvent_id(eventDTO.getEvent().getID());
                orderResponseDTO.setEvent_name(eventDTO.getEvent().getName());
                orderResponseDTO.setOrder(order);

                ordersDTO.add(orderResponseDTO);
            });
        }

        return ordersDTO;
    }


    public  ByteArrayOutputStream generatePdfStream(int month, int year) throws DocumentException, IOException {

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        List<Order> orderList = repository.findOrdersByMonthAndYear(month, year);

        int total_orders = 0;
        Double total_revenue = 0.0;

        for (Order order: orderList) {
            total_orders++;
            total_revenue += order.getTotal_price();
        }

        URL imageUrl = new URL("https://grupo7-bucket.s3.amazonaws.com/logo.jpg");
        Image img = Image.getInstance(imageUrl);
        img.scaleToFit(200, 150);

        document.add(img);

        document.add(new Paragraph("\n"));

        String monthW = orderList.get(1).getDate_time().getMonth().name();
        int yearW = orderList.get(1).getDate_time().getYear();

        Paragraph title =new Paragraph("BUSINESS REPORT OF "+monthW+ " " + yearW, FontFactory.getFont(FontFactory.HELVETICA,24));
        title.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(title);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        String formattedRevenue = String.format("%.2f", total_revenue);
        document.add(new Paragraph("The total revenue for the month is $"+ formattedRevenue));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("The number of orders for the month is "+ total_orders));
        document.add(new Paragraph("\n"));

        long uniqueUserCount = countUniqueUsers(orderList);

        document.add(new Paragraph("The number unique users this month was "+ uniqueUserCount));
        document.add(new Paragraph("\n"));
        String formattedAvgOrder = String.format("%.2f", total_revenue/total_orders);
        document.add(new Paragraph("The average spending per user was $"+ formattedAvgOrder));
        document.add(new Paragraph("\n"));
        String formattedSpending = String.format("%.2f", total_revenue/uniqueUserCount);
        document.add(new Paragraph("The average spending per order was $"+ formattedSpending));

        document.close();
        return outputStream;
    }

    public static long countUniqueUsers(List<Order> orderList) {
        Set<Long> uniqueUserIds = orderList.stream()
                .map(Order::getUserid)
                .collect(Collectors.toSet());

        return uniqueUserIds.size();
    }

}
