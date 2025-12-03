-- A1노선 추가 (ID=1)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (1, 'A1노선', '기흥역', '이공관');
-- A1노선의 정류장 목록 추가
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 0, '기흥역');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 1, '강남대역');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 2, '샬롬관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 3, '본관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 4, '이공관');

-- A2노선 추가 (ID=2)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (2, 'A2노선', '이공관', '기흥역');
-- A2노선의 정류장 목록 추가
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (2, 0, '이공관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (2, 1, '본관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (2, 2, '인사관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (2, 3, '스타벅스 앞');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (2, 4, '기흥역');

-- B1노선 추가 (ID=3)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (3, 'B1노선', '스타벅스 앞', '이공관');
-- B1노선의 정류장 목록 추가
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (3, 0, '스타벅스 앞');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (3, 1, '샬롬관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (3, 2, '본관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (3, 3, '이공관');

-- B2노선 추가 (ID=4)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (4, 'B2노선', '이공관', '스타벅스 앞');
-- B2노선의 정류장 목록 추가
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (4, 0, '이공관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (4, 1, '본관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (4, 2, '인사관');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (4, 3, '스타벅스 앞');

-- A노선 시간표 추가
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:00:00', 'DG01');
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:20:00', 'DG02');
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:40:00', 'DG03');
