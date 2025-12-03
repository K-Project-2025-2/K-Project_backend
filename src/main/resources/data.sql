-- A노선 추가 (ID=1)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (1, 'A노선', '기흥역', '강남대학교 정문');
-- A노선의 정류장 목록 추가
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 0, '기흥역');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 1, '강남대역');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 2, '기숙사');
INSERT INTO `shuttle_route_stations` (`route_id`, `station_order`, `station_name`) VALUES (1, 3, '정문');

-- B노선 추가 (ID=2)
INSERT INTO `shuttle_routes` (`id`, `name`, `origin`, `destination`) VALUES (2, 'B노선', '강남대학교', '기흥역');

-- A노선 시간표 추가
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:00:00', 'DG01');
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:20:00', 'DG02');
INSERT INTO `shuttle_timetables` (`route_id`, `departure_time`, `bus_number`) VALUES (1, '08:40:00', 'DG03');
