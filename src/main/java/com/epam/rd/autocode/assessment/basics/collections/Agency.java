package com.epam.rd.autocode.assessment.basics.collections;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.epam.rd.autocode.assessment.basics.entity.BodyType;
import com.epam.rd.autocode.assessment.basics.entity.Client;
import com.epam.rd.autocode.assessment.basics.entity.Order;
import com.epam.rd.autocode.assessment.basics.entity.Vehicle;

public class Agency implements Find, Sort {

	private List<Vehicle> vehicles;

	private List<Order> orders;

	public Agency() {
		vehicles = new ArrayList<>();
		orders = new ArrayList<>();
	}

	public void addVehicle(Vehicle vehicle){
		vehicles.add(vehicle);
	}

	public void addOrder(Order order){
		orders.add(order);
	}

	@Override
	public List<Vehicle> sortByID() {
		return vehicles.stream().
						sorted(Comparator.comparingLong(Vehicle::getId)).
						collect(Collectors.toList());
	}

	@Override
	public List<Vehicle> sortByYearOfProduction() {
		return vehicles.stream().
						sorted(Comparator.comparingInt(Vehicle::getYearOfProduction)).
						collect(Collectors.toList());
	}

	@Override
	public List<Vehicle> sortByOdometer() {
		return vehicles.stream().
						sorted(Comparator.comparingLong(Vehicle::getOdometer)).
						collect(Collectors.toList());
	}

	@Override
	public Set<String> findMakers() {
		return vehicles.stream().
						map(Vehicle::getMake).
						collect(Collectors.toSet());
	}

	@Override
	public Set<BodyType> findBodytypes() {
		return vehicles.stream().
						map(Vehicle::getBodyType).
						collect(Collectors.toSet());
	}

	@Override
	public Map<String, List<Vehicle>> findVehicleGrouppedByMake() {
		return vehicles.stream()
						.collect(Collectors.groupingBy(Vehicle::getMake));
	}

	@Override
	public List<Client> findTopClientsByPrices(List<Client> clients, int maxCount) {
		return clients.stream().
						sorted(Comparator.comparing(Client::getBalance).reversed()).
						limit(maxCount).collect(Collectors.toList());
	}

	@Override
	public List<Client> findClientsWithAveragePriceNoLessThan(List<Client> clients, int average) {

		return clients.stream()
				.filter(client -> {
					BigDecimal totalOrderPrice = orders.stream().filter(order -> order.getClientId() == client.getId())
							.map(Order::getPrice)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					BigDecimal averagePrice = totalOrderPrice.divide(BigDecimal.valueOf(orders.stream().filter(order -> order.getClientId() == client.getId()).toList().size()), 2, BigDecimal.ROUND_HALF_UP);
					return averagePrice.compareTo(BigDecimal.valueOf(average)) >= 0;
				})
				.collect(Collectors.toList());

	}

	@Override
	public List<Vehicle> findMostOrderedVehicles(int maxCount) {
		return orders.stream()
				.collect(Collectors.groupingBy(Order::getVehicleId, Collectors.summingInt(order -> 1)))
				.entrySet().stream()
				.sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
				.limit(maxCount)
				.map(entry -> vehicles.stream().filter(vehicle -> vehicle.getId() == entry.getKey()).findFirst().orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

	}

}