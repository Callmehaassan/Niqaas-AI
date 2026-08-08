import React, { useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TextInput,
  TouchableOpacity,
  SafeAreaView,
  StatusBar
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface CitizenReportProps {
  onBack: () => void;
  onSubmit: () => void;
}

export default function CitizenReport({ onBack, onSubmit }: CitizenReportProps) {
  const [description, setDescription] = useState('');
  const [isFocused, setIsFocused] = useState(false);
  const [selectedSeverity, setSelectedSeverity] = useState<'Passable' | 'Blocked' | 'Emergency'>('Passable');

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      {/* Top Bar */}
      <View style={styles.topBar}>
        <TouchableOpacity onPress={onBack} style={styles.backButton}>
          <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>Report Flood</Text>
        <View style={styles.badgeContainer}>
          <View style={styles.amberBadge}>
            <Text style={styles.amberBadgeText}>Heavy Rain — Islamabad</Text>
          </View>
        </View>
      </View>

      {/* Body */}
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Location Card */}
        <View style={styles.locationCard}>
          <View style={styles.locationHeader}>
            <Text style={styles.mutedLabel}>YOUR LOCATION</Text>
            <TouchableOpacity>
              <Text style={styles.tealLink}>Adjust</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.locationDetails}>
            <Ionicons name="location-sharp" size={20} color="#00C2B2" />
            <Text style={styles.locationText}>G-10, Islamabad</Text>
          </View>
          <View style={styles.locationBottomBorder} />
        </View>

        {/* Space */}
        <View style={styles.spacer} />

        {/* Description Input */}
        <View style={styles.inputGroup}>
          <Text style={styles.mutedLabel}>Describe the flooding</Text>
          <TextInput
            style={[
              styles.textArea,
              isFocused ? styles.textAreaFocused : styles.textAreaUnfocused
            ]}
            placeholder="e.g. Road blocked, water above knee level"
            placeholderTextColor="#8899AA"
            multiline
            numberOfLines={4}
            value={description}
            onChangeText={setDescription}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
          />
        </View>

        {/* Space */}
        <View style={styles.spacer} />

        {/* Severity Chips */}
        <View style={styles.severitySection}>
          <Text style={styles.mutedLabel}>Select severity</Text>
          <View style={styles.severityRow}>
            <TouchableOpacity
              style={[
                styles.severityChip,
                selectedSeverity === 'Passable'
                  ? styles.severityChipPassable
                  : styles.severityChipUnselected
              ]}
              onPress={() => setSelectedSeverity('Passable')}
            >
              <Text style={styles.severityText}>Passable</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[
                styles.severityChip,
                selectedSeverity === 'Blocked'
                  ? styles.severityChipBlocked
                  : styles.severityChipUnselected
              ]}
              onPress={() => setSelectedSeverity('Blocked')}
            >
              <Text style={styles.severityText}>Blocked</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[
                styles.severityChip,
                selectedSeverity === 'Emergency'
                  ? styles.severityChipEmergency
                  : styles.severityChipUnselected
              ]}
              onPress={() => setSelectedSeverity('Emergency')}
            >
              <Text style={styles.severityText}>Emergency</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Space */}
        <View style={styles.spacer} />

        {/* Photo Upload Card */}
        <TouchableOpacity style={styles.photoCard} activeOpacity={0.8}>
          <Ionicons name="camera-outline" size={28} color="#00C2B2" />
          <Text style={styles.photoLabel}>Add Photo (optional)</Text>
        </TouchableOpacity>
      </ScrollView>

      {/* Fixed Bottom */}
      <View style={styles.bottomContainer}>
        <TouchableOpacity style={styles.submitButton} onPress={onSubmit} activeOpacity={0.9}>
          <Text style={styles.submitButtonText}>Submit Report</Text>
        </TouchableOpacity>
        <Text style={styles.nearReportsText}>3 other reports near you in last 20 min</Text>
      </View>
    </SafeAreaView>
  );
}

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
  badgeContainer: {
    justifyContent: 'center',
  },
  amberBadge: {
    backgroundColor: '#F5A623',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  amberBadgeText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  scrollContent: {
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 100, // Make room for fixed bottom
  },
  locationCard: {
    backgroundColor: '#1E3248',
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#2A4060',
    position: 'relative',
    overflow: 'hidden',
  },
  locationHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  mutedLabel: {
    fontSize: 11,
    color: '#8899AA',
    fontWeight: 'bold',
    letterSpacing: 1,
    marginBottom: 6,
  },
  tealLink: {
    fontSize: 13,
    color: '#00C2B2',
    fontWeight: 'bold',
  },
  locationDetails: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  locationText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginLeft: 6,
  },
  locationBottomBorder: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 2,
    backgroundColor: '#00C2B2',
  },
  spacer: {
    height: 12,
  },
  inputGroup: {
    width: '100%',
  },
  textArea: {
    backgroundColor: '#1E3248',
    borderRadius: 8,
    padding: 12,
    color: '#FFFFFF',
    fontSize: 14,
    minHeight: 100,
    borderWidth: 2,
    textAlignVertical: 'top',
  },
  textAreaUnfocused: {
    borderColor: '#2A4060',
  },
  textAreaFocused: {
    borderColor: '#00C2B2',
  },
  severitySection: {
    width: '100%',
  },
  severityRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 4,
  },
  severityChip: {
    width: '31%',
    height: 44,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
  },
  severityChipUnselected: {
    backgroundColor: '#1E3248',
    borderWidth: 1,
    borderColor: '#2A4060',
  },
  severityChipPassable: {
    backgroundColor: '#00C2B2',
  },
  severityChipBlocked: {
    backgroundColor: '#F5A623',
  },
  severityChipEmergency: {
    backgroundColor: '#E84040',
  },
  severityText: {
    color: '#FFFFFF',
    fontSize: 13,
    fontWeight: 'bold',
  },
  photoCard: {
    height: 80,
    backgroundColor: '#1E3248',
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: '#00C2B2',
    borderStyle: 'dashed',
    justifyContent: 'center',
    alignItems: 'center',
  },
  photoLabel: {
    fontSize: 12,
    color: '#8899AA',
    marginTop: 6,
  },
  bottomContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#0B1F3A',
    borderTopWidth: 1,
    borderColor: '#2A4060',
    alignItems: 'center',
  },
  submitButton: {
    width: '100%',
    height: 56,
    backgroundColor: '#00C2B2',
    justifyContent: 'center',
    alignItems: 'center',
  },
  submitButtonText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  nearReportsText: {
    fontSize: 12,
    color: '#8899AA',
    marginVertical: 12,
  },
});
