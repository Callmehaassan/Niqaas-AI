import React, { useState, useEffect, useRef } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
  Dimensions,
  Platform,
  Animated
} from 'react-native';
import { Ionicons, FontAwesome5, MaterialCommunityIcons } from '@expo/vector-icons';
import MapView, { Marker, Polygon, Polyline, Circle } from 'react-native-maps';

interface IncidentDetailProps {
  onBack: () => void;
}

export default function IncidentDetail({ onBack }: IncidentDetailProps) {
  const [toggleState, setToggleState] = useState<'Before' | 'After'>('Before');
  const pulseAnim = useRef(new Animated.Value(0.6)).current;

  // Pulsing animation for the Before state flood marker
  useEffect(() => {
    if (toggleState === 'Before') {
      Animated.loop(
        Animated.sequence([
          Animated.timing(pulseAnim, {
            toValue: 1.0,
            duration: 1000,
            useNativeDriver: true
          }),
          Animated.timing(pulseAnim, {
            toValue: 0.6,
            duration: 1000,
            useNativeDriver: true
          })
        ])
      ).start();
    } else {
      pulseAnim.setValue(0.6);
    }
  }, [toggleState]);

  // Islamabad G-10 Map Coordinates
  const mapRegion = {
    latitude: 33.6784,
    longitude: 72.9972,
    latitudeDelta: 0.025,
    longitudeDelta: 0.025
  };

  const g10Polygon = [
    { latitude: 33.690, longitude: 72.985 },
    { latitude: 33.690, longitude: 73.010 },
    { latitude: 33.665, longitude: 73.010 },
    { latitude: 33.665, longitude: 72.985 }
  ];

  const reroutePath = [
    { latitude: 33.670, longitude: 72.990 }, // Start before flood point
    { latitude: 33.670, longitude: 73.005 }, // Reroute right
    { latitude: 33.685, longitude: 73.005 }, // Up Kashmir Highway Service Rd
    { latitude: 33.685, longitude: 72.990 }  // Back left
  ];

  const floodPoint = { latitude: 33.6784, longitude: 72.9972 };
  const drainTruck = { latitude: 33.674, longitude: 72.994 };

  const renderSimulatedMap = () => {
    return (
      <View style={styles.simulatedMap}>
        <View style={styles.simulatedGrid}>
          {/* Islamabad Roads */}
          <View style={styles.simRoadHorizontal} />
          <View style={[styles.simRoadHorizontal, { top: '75%' }]} />
          <View style={styles.simRoadVertical} />
          <View style={[styles.simRoadVertical, { left: '75%' }]} />

          {/* Polygon Overlay */}
          <View
            style={[
              styles.simPolygon,
              toggleState === 'Before'
                ? { backgroundColor: 'rgba(232, 64, 64, 0.25)', borderColor: '#E84040' }
                : { backgroundColor: 'rgba(232, 64, 64, 0.08)', borderColor: 'rgba(232, 64, 64, 0.4)' }
            ]}
          />

          {/* Before: Pulsing Flood Marker */}
          {toggleState === 'Before' && (
            <Animated.View style={[styles.simFloodPoint, { transform: [{ scale: pulseAnim }] }]}>
              <View style={styles.simFloodPointInner} />
            </Animated.View>
          )}

          {/* After: Reroute path, Truck, Radius */}
          {toggleState === 'After' && (
            <>
              {/* Alert Radius */}
              <View style={styles.simAlertRadius} />
              
              {/* Reroute Line */}
              <View style={styles.simRerouteLine} />

              {/* Truck Marker */}
              <View style={styles.simTruckMarker}>
                <Ionicons name="truck" size={16} color="#FFFFFF" />
              </View>
            </>
          )}

          {/* Marker label */}
          <View style={[styles.simLabelCard, toggleState === 'Before' ? { top: '40%' } : { top: '30%' }]}>
            <Text style={styles.simLabelText}>
              {toggleState === 'Before' ? 'G-10 Underpass Flooded' : 'Reroute active via Service Rd'}
            </Text>
          </View>
        </View>
        <Text style={styles.simMapFooter}>[ Simulated Map View - Web Fallback ]</Text>
      </View>
    );
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      {/* Top Bar */}
      <View style={styles.topBar}>
        <TouchableOpacity onPress={onBack} style={styles.backButton}>
          <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.topBarTitle} numberOfLines={1}>G-10 Underpass</Text>
        <View style={[styles.statusChip, styles.chipCritical, { marginLeft: 8 }]}>
          <Text style={styles.statusChipText}>CRITICAL</Text>
        </View>
      </View>

      {/* Main Container */}
      <View style={styles.container}>
        {/* Map Section */}
        <View style={styles.mapContainer}>
          {Platform.OS === 'web' ? (
            renderSimulatedMap()
          ) : (
            <MapView
              style={styles.map}
              initialRegion={mapRegion}
              customMapStyle={darkMapStyle}
            >
              {/* G-10 Area Polygon */}
              <Polygon
                coordinates={g10Polygon}
                fillColor={toggleState === 'Before' ? 'rgba(232, 64, 64, 0.25)' : 'rgba(232, 64, 64, 0.08)'}
                strokeColor={toggleState === 'Before' ? '#E84040' : 'rgba(232, 64, 64, 0.4)'}
                strokeWidth={2}
              />

              {/* Before State elements */}
              {toggleState === 'Before' && (
                <Marker coordinate={floodPoint} anchor={{ x: 0.5, y: 0.5 }}>
                  <Animated.View style={[styles.pulsingMarker, { transform: [{ scale: pulseAnim }] }]}>
                    <View style={styles.pulseDot} />
                  </Animated.View>
                </Marker>
              )}

              {/* After State elements */}
              {toggleState === 'After' && (
                <>
                  {/* Alert Radius */}
                  <Circle
                    center={floodPoint}
                    radius={700}
                    fillColor="rgba(0, 194, 178, 0.1)"
                    strokeColor="#00C2B2"
                    strokeWidth={1}
                  />

                  {/* Reroute path */}
                  <Polyline
                    coordinates={reroutePath}
                    strokeColor="#00C2B2"
                    strokeWidth={3}
                    lineDashPattern={[6, 6]}
                  />

                  {/* Drain Truck */}
                  <Marker coordinate={drainTruck} title="Drainage Cleansing Team">
                    <View style={styles.truckIconWrapper}>
                      <Ionicons name="truck" size={14} color="#FFFFFF" />
                    </View>
                  </Marker>
                </>
              )}
            </MapView>
          )}

          {/* Toggle pill centered over map */}
          <View style={styles.togglePillWrapper}>
            <View style={styles.togglePill}>
              <TouchableOpacity
                style={[styles.toggleOption, toggleState === 'Before' && styles.toggleOptionActive]}
                onPress={() => setToggleState('Before')}
              >
                <Text style={[styles.toggleText, toggleState === 'Before' && styles.toggleTextActive]}>
                  Before
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.toggleOption, toggleState === 'After' && styles.toggleOptionActive]}
                onPress={() => setToggleState('After')}
              >
                <Text style={[styles.toggleText, toggleState === 'After' && styles.toggleTextActive]}>
                  After
                </Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>

        {/* Scrollable details below map */}
        <ScrollView contentContainerStyle={styles.scrollContent}>
          {/* Stats Row */}
          <View style={styles.statsRow}>
            {/* Card 1 */}
            <View style={styles.statCard}>
              <Text style={styles.statLabel}>Congestion</Text>
              <Text
                style={[
                  styles.statValue,
                  toggleState === 'Before' ? styles.textRed : styles.textAmber
                ]}
              >
                {toggleState === 'Before' ? 'HIGH' : 'MOD'}
              </Text>
            </View>

            {/* Card 2 */}
            <View style={styles.statCard}>
              <Text style={styles.statLabel}>Speed</Text>
              <Text style={styles.statValue}>
                {toggleState === 'Before' ? '8km/h' : '35km/h'}
              </Text>
            </View>

            {/* Card 3 */}
            <View style={styles.statCard}>
              <Text style={styles.statLabel}>Alerts</Text>
              <Text style={styles.statValue}>
                {toggleState === 'Before' ? '0' : '240'}
              </Text>
            </View>

            {/* Card 4 */}
            <View style={styles.statCard}>
              <Text style={styles.statLabel}>Ticket</Text>
              <Text
                style={[
                  styles.statValue,
                  toggleState === 'Before' ? styles.textGray : styles.textGreen
                ]}
              >
                {toggleState === 'Before' ? '—' : 'Open'}
              </Text>
            </View>
          </View>

          <View style={styles.spacer} />

          {/* Dispatch Ticket Card */}
          <View style={styles.infoCard}>
            <Text style={styles.cardHeader}>Dispatch Ticket #0042</Text>
            
            {/* Connected status lifecycle dots */}
            <View style={styles.lifecycleRow}>
              {/* Point 1 */}
              <View style={styles.lifecycleStep}>
                <View style={styles.lifecycleDotFilled} />
                <Text style={styles.lifecycleLabel}>Created</Text>
              </View>

              {/* Line 1 */}
              <View style={[styles.lifecycleLine, styles.lifecycleLineActive]} />

              {/* Point 2 */}
              <View style={styles.lifecycleStep}>
                <View style={styles.lifecycleDotFilled} />
                <Text style={styles.lifecycleLabel}>In Progress</Text>
              </View>

              {/* Line 2 */}
              <View style={[styles.lifecycleLine, styles.lifecycleLineInactive]} />

              {/* Point 3 */}
              <View style={styles.lifecycleStep}>
                <View style={styles.lifecycleDotEmpty} />
                <Text style={styles.lifecycleLabel}>Resolved</Text>
              </View>
            </View>

            <Text style={styles.ticketDetails}>Unit 3 · 3:01 PM · G-10 Drain Team</Text>
          </View>

          <View style={styles.spacer} />

          {/* Reasoning Trail Card */}
          <View style={[styles.infoCard, styles.cardTealBorder]}>
            <Text style={styles.tealLabel}>AI REASONING TRAIL</Text>
            <Text style={styles.cardSubheader}>94% confidence — CRITICAL</Text>
            <Text style={styles.reasoningItalic}>
              Aggregated 4 citizen reports of deep underpass flood water blocking motors. PMD active rainfall telemetry confirmed 20mm/hr precipitation. Traffic loops reported average speeds dropped to 8km/h. Recommendation engine triggered rerouting vectors immediately.
            </Text>

            <View style={styles.pillsRow}>
              <View style={styles.tealPill}>
                <Text style={styles.tealPillText}>4 Citizens</Text>
              </View>
              <View style={styles.tealPill}>
                <Text style={styles.tealPillText}>Rain Alert</Text>
              </View>
              <View style={styles.tealPill}>
                <Text style={styles.tealPillText}>Traffic ×3.4</Text>
              </View>
            </View>
          </View>

          <View style={{ height: 40 }} />
        </ScrollView>
      </View>
    </SafeAreaView>
  );
}

// Google Maps Dark Style Config
const darkMapStyle = [
  { elementType: 'geometry', stylers: [{ color: '#0B1F3A' }] },
  { elementType: 'labels.text.stroke', stylers: [{ color: '#0B1F3A' }] },
  { elementType: 'labels.text.fill', stylers: [{ color: '#8899AA' }] },
  { featureType: 'administrative.locality', elementType: 'labels.text.fill', stylers: [{ color: '#FFFFFF' }] },
  { featureType: 'poi', elementType: 'labels.text.fill', stylers: [{ color: '#8899AA' }] },
  { featureType: 'road', stylers: [{ color: '#1E3248' }] },
  { featureType: 'road', elementType: 'geometry.stroke', stylers: [{ color: '#2A4060' }] },
  { featureType: 'road', elementType: 'labels.text.fill', stylers: [{ color: '#8899AA' }] },
  { featureType: 'water', stylers: [{ color: '#091524' }] }
];

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#0B1F3A',
  },
  topBar: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderColor: '#2A4060',
  },
  backButton: {
    width: 32,
    justifyContent: 'center',
  },
  topBarTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    flex: 1,
    marginLeft: 8,
  },
  statusChip: {
    borderRadius: 6,
    paddingHorizontal: 6,
    paddingVertical: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  chipCritical: {
    backgroundColor: '#E84040',
  },
  statusChipText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  container: {
    flex: 1,
  },
  mapContainer: {
    height: '45%',
    width: '100%',
    position: 'relative',
  },
  map: {
    ...StyleSheet.absoluteFillObject,
  },
  togglePillWrapper: {
    position: 'absolute',
    top: 16,
    left: 0,
    right: 0,
    alignItems: 'center',
    zIndex: 10,
  },
  togglePill: {
    flexDirection: 'row',
    backgroundColor: '#1E3248',
    borderRadius: 20,
    padding: 2,
    borderWidth: 1,
    borderColor: '#2A4060',
    width: 160,
  },
  toggleOption: {
    flex: 1,
    paddingVertical: 6,
    alignItems: 'center',
    borderRadius: 18,
  },
  toggleOptionActive: {
    backgroundColor: '#00C2B2',
  },
  toggleText: {
    color: '#8899AA',
    fontSize: 12,
    fontWeight: 'bold',
  },
  toggleTextActive: {
    color: '#FFFFFF',
  },
  pulsingMarker: {
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: 'rgba(232, 64, 64, 0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  pulseDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
    backgroundColor: '#E84040',
  },
  truckIconWrapper: {
    width: 24,
    height: 24,
    borderRadius: 6, // 6px radius from specs
    backgroundColor: '#F5A623',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#FFFFFF',
  },
  scrollContent: {
    paddingHorizontal: 16,
    paddingTop: 16,
  },
  statsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  statCard: {
    width: '23%',
    backgroundColor: '#1E3248',
    paddingVertical: 10,
    paddingHorizontal: 6,
    borderRadius: 8,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#2A4060',
  },
  statLabel: {
    fontSize: 10,
    color: '#8899AA',
    fontWeight: 'bold',
    marginBottom: 4,
  },
  statValue: {
    fontSize: 13,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  textRed: {
    color: '#E84040',
  },
  textAmber: {
    color: '#F5A623',
  },
  textGray: {
    color: '#8899AA',
  },
  textGreen: {
    color: '#00C2B2', // Teal used as green in stats
  },
  spacer: {
    height: 12,
  },
  infoCard: {
    backgroundColor: '#1E3248',
    padding: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#2A4060',
  },
  cardTealBorder: {
    borderLeftWidth: 3,
    borderLeftColor: '#00C2B2',
  },
  cardHeader: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
  },
  cardSubheader: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginTop: 4,
    marginBottom: 8,
  },
  lifecycleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 12,
    marginBottom: 16,
  },
  lifecycleStep: {
    alignItems: 'center',
    width: 60,
  },
  lifecycleDotFilled: {
    width: 14,
    height: 14,
    borderRadius: 7,
    backgroundColor: '#00C2B2',
  },
  lifecycleDotEmpty: {
    width: 14,
    height: 14,
    borderRadius: 7,
    borderWidth: 2,
    borderColor: '#2A4060',
    backgroundColor: '#1E3248',
  },
  lifecycleLabel: {
    fontSize: 9,
    color: '#8899AA',
    fontWeight: 'bold',
    marginTop: 6,
  },
  lifecycleLine: {
    flex: 1,
    height: 2,
    top: -8, // Align with dot centers
  },
  lifecycleLineActive: {
    backgroundColor: '#00C2B2',
  },
  lifecycleLineInactive: {
    backgroundColor: '#2A4060',
  },
  ticketDetails: {
    fontSize: 12,
    color: '#8899AA',
  },
  tealLabel: {
    fontSize: 11,
    color: '#00C2B2',
    fontWeight: 'bold',
    letterSpacing: 1,
  },
  reasoningItalic: {
    fontSize: 12,
    fontStyle: 'italic',
    color: '#8899AA',
    lineHeight: 18,
    marginBottom: 12,
  },
  pillsRow: {
    flexDirection: 'row',
  },
  tealPill: {
    backgroundColor: 'rgba(0, 194, 178, 0.12)',
    borderRadius: 6, // 6px radius for status chips
    paddingHorizontal: 8,
    paddingVertical: 4,
    marginRight: 8,
    borderWidth: 1,
    borderColor: '#00C2B2',
  },
  tealPillText: {
    color: '#00C2B2',
    fontSize: 10,
    fontWeight: 'bold',
  },

  // Simulated Web Map styles
  simulatedMap: {
    flex: 1,
    backgroundColor: '#0B1F3A',
    justifyContent: 'center',
    alignItems: 'center',
  },
  simulatedGrid: {
    width: '90%',
    height: '80%',
    backgroundColor: '#0F2544',
    borderWidth: 1,
    borderColor: '#2A4060',
    position: 'relative',
    overflow: 'hidden',
  },
  simRoadHorizontal: {
    position: 'absolute',
    top: '48%',
    left: 0,
    right: 0,
    height: 16,
    backgroundColor: '#1E3248',
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#2A4060',
  },
  simRoadVertical: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: '48%',
    width: 16,
    backgroundColor: '#1E3248',
    borderLeftWidth: 1,
    borderRightWidth: 1,
    borderColor: '#2A4060',
  },
  simPolygon: {
    position: 'absolute',
    top: '35%',
    left: '35%',
    width: '35%',
    height: '35%',
    borderWidth: 1.5,
  },
  simFloodPoint: {
    position: 'absolute',
    top: '52%',
    left: '52%',
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: 'rgba(232, 64, 64, 0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: -12,
    marginTop: -12,
  },
  simFloodPointInner: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#E84040',
  },
  simAlertRadius: {
    position: 'absolute',
    top: '25%',
    left: '25%',
    width: '54%',
    height: '54%',
    borderRadius: 100,
    borderWidth: 1,
    borderColor: '#00C2B2',
    backgroundColor: 'rgba(0, 194, 178, 0.08)',
  },
  simRerouteLine: {
    position: 'absolute',
    top: '50%',
    left: '10%',
    width: '80%',
    height: 60,
    borderLeftWidth: 2,
    borderBottomWidth: 2,
    borderRightWidth: 2,
    borderColor: '#00C2B2',
    borderStyle: 'dashed',
  },
  simTruckMarker: {
    position: 'absolute',
    top: '73%',
    left: '40%',
    width: 24,
    height: 24,
    borderRadius: 6,
    backgroundColor: '#F5A623',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#FFFFFF',
  },
  simLabelCard: {
    position: 'absolute',
    left: '5%',
    right: '5%',
    backgroundColor: '#1E3248',
    padding: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#00C2B2',
    alignItems: 'center',
  },
  simLabelText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  simMapFooter: {
    fontSize: 9,
    color: '#8899AA',
    marginTop: 4,
  },
});
