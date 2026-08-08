import React from 'react';
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  StatusBar
} from 'react-native';
import { Ionicons, FontAwesome5 } from '@expo/vector-icons';

interface AuthorityDashboardProps {
  onBack: () => void;
  onSelectIncident: (name: string) => void;
  onSimulateFullResponse: () => void;
}

export default function AuthorityDashboard({
  onBack,
  onSelectIncident,
  onSimulateFullResponse
}: AuthorityDashboardProps) {
  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      {/* Top Bar */}
      <View style={styles.topBar}>
        <TouchableOpacity onPress={onBack} style={styles.iconButton}>
          <Ionicons name="menu" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>Nikaas نکاس — Authority</Text>
        <TouchableOpacity style={styles.iconButton}>
          <View style={styles.notificationWrapper}>
            <Ionicons name="notifications" size={22} color="#E84040" />
            <View style={styles.notificationBadge}>
              <Text style={styles.notificationBadgeText}>7</Text>
            </View>
          </View>
        </TouchableOpacity>
      </View>

      {/* Ticker Strip */}
      <View style={styles.tickerStrip}>
        <View style={styles.amberDot} />
        <Text style={styles.tickerText} numberOfLines={1}>
          Islamabad — Active Monsoon | 47mm rainfall | 7 active incidents
        </Text>
      </View>

      {/* Body Scroll */}
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Incident List Header */}
        <Text style={styles.sectionHeader}>ACTIVE INCIDENTS</Text>

        {/* Horizontal Incident List */}
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalScrollContent}
        >
          {/* Card 1 */}
          <TouchableOpacity
            style={[styles.incidentCard, styles.incidentCardCritical]}
            onPress={() => onSelectIncident('G-10 Underpass')}
            activeOpacity={0.8}
          >
            <View style={styles.incidentCardHeader}>
              <Text style={styles.incidentTitle}>G-10 Underpass</Text>
              <Text style={styles.timeText}>2 min ago</Text>
            </View>
            <View style={styles.chipRow}>
              <View style={[styles.statusChip, styles.chipCritical]}>
                <Text style={styles.statusChipText}>CRITICAL</Text>
              </View>
            </View>
            <Text style={styles.incidentDetails}>4 reports · Rain · Traffic</Text>
          </TouchableOpacity>

          {/* Card 2 */}
          <TouchableOpacity
            style={[styles.incidentCard, styles.incidentCardHigh]}
            onPress={() => onSelectIncident('I-8 Markaz')}
            activeOpacity={0.8}
          >
            <View style={styles.incidentCardHeader}>
              <Text style={styles.incidentTitle}>I-8 Markaz</Text>
            </View>
            <View style={styles.chipRow}>
              <View style={[styles.statusChip, styles.chipWarning]}>
                <Text style={styles.statusChipText}>HIGH</Text>
              </View>
            </View>
            <Text style={styles.incidentDetails}>2 reports · Rain</Text>
          </TouchableOpacity>

          {/* Card 3 */}
          <TouchableOpacity
            style={[styles.incidentCard, styles.incidentCardMedium]}
            onPress={() => onSelectIncident('G-11 Sector')}
            activeOpacity={0.8}
          >
            <View style={styles.incidentCardHeader}>
              <Text style={styles.incidentTitle}>G-11 Sector</Text>
            </View>
            <View style={styles.chipRow}>
              <View style={[styles.statusChip, styles.chipMuted]}>
                <Text style={styles.statusChipText}>MEDIUM</Text>
              </View>
            </View>
            <Text style={styles.incidentDetails}>1 report</Text>
          </TouchableOpacity>
        </ScrollView>

        <View style={styles.spacer} />

        {/* AI Assessment Card */}
        <View style={styles.aiAssessmentCard}>
          <Text style={styles.tealLabel}>AI SEVERITY ASSESSMENT</Text>
          
          <Text style={styles.assessmentSeverity}>CRITICAL</Text>
          <Text style={styles.assessmentConfidence}>94% confidence</Text>

          <View style={styles.divider} />

          {/* Signal Rows */}
          <View style={styles.signalRow}>
            <Ionicons name="checkmark-circle" size={16} color="#00C2B2" />
            <Text style={styles.signalText}>Citizens — 4 reports confirmed</Text>
          </View>
          <View style={styles.signalRow}>
            <Ionicons name="checkmark-circle" size={16} color="#00C2B2" />
            <Text style={styles.signalText}>Weather — Active rainfall alert</Text>
          </View>
          <View style={styles.signalRow}>
            <Ionicons name="checkmark-circle" size={16} color="#00C2B2" />
            <Text style={styles.signalText}>Traffic — 340% congestion spike</Text>
          </View>

          <View style={styles.divider} />

          <Text style={styles.mutedLabelCaps}>REASONING</Text>
          <Text style={styles.reasoningItalic}>
            High confidence: 4 citizen reports + active monsoon alert + 340% traffic spike on Kashmir Highway in last 20 minutes
          </Text>
        </View>

        <View style={styles.spacer} />

        {/* Response Actions Header */}
        <Text style={styles.sectionHeader}>PROPOSED EMERGENCY ACTIONS</Text>

        {/* Stacked Cards */}
        {/* Action 1 */}
        <View style={[styles.actionCard, styles.actionTealBorder]}>
          <View style={styles.actionCardContent}>
            <Text style={styles.actionTitle}>Reroute Traffic</Text>
            <Text style={styles.actionDesc}>Via Margalla Road</Text>
          </View>
          <View style={[styles.statusChip, styles.chipTealBG]}>
            <Text style={styles.statusChipText}>Simulated</Text>
          </View>
        </View>

        <View style={styles.spacerBetweenCards} />

        {/* Action 2 */}
        <View style={[styles.actionCard, styles.actionAmberBorder]}>
          <View style={styles.actionCardContent}>
            <Text style={styles.actionTitle}>Dispatch Drain Team</Text>
            <Text style={styles.actionDesc}>Unit 3 — ETA 12 min</Text>
          </View>
          <View style={[styles.statusChip, styles.chipWarning]}>
            <Text style={styles.statusChipText}>Executing</Text>
          </View>
        </View>

        <View style={styles.spacerBetweenCards} />

        {/* Action 3 */}
        <View style={[styles.actionCard, styles.actionBlueBorder]}>
          <View style={styles.actionCardContent}>
            <Text style={styles.actionTitle}>Alert Residents</Text>
            <Text style={styles.actionDesc}>240 residents in G-10/G-11</Text>
          </View>
          <View style={[styles.statusChip, styles.chipMuted]}>
            <Text style={styles.statusChipText}>Pending</Text>
          </View>
        </View>
      </ScrollView>

      {/* Fixed Bottom */}
      <TouchableOpacity style={styles.simulateButton} onPress={onSimulateFullResponse} activeOpacity={0.9}>
        <Text style={styles.simulateButtonText}>Simulate Full Response</Text>
      </TouchableOpacity>
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
  iconButton: {
    width: 32,
    height: 32,
    justifyContent: 'center',
    alignItems: 'center',
  },
  topBarTitle: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#FFFFFF',
    flex: 1,
    textAlign: 'center',
  },
  notificationWrapper: {
    position: 'relative',
  },
  notificationBadge: {
    position: 'absolute',
    top: -4,
    right: -4,
    backgroundColor: '#E84040',
    width: 14,
    height: 14,
    borderRadius: 7,
    justifyContent: 'center',
    alignItems: 'center',
  },
  notificationBadgeText: {
    color: '#FFFFFF',
    fontSize: 9,
    fontWeight: 'bold',
  },
  tickerStrip: {
    height: 36,
    backgroundColor: '#1E3248',
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderColor: '#2A4060',
  },
  amberDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: '#F5A623',
    marginRight: 8,
  },
  tickerText: {
    fontSize: 12,
    color: '#8899AA',
  },
  scrollContent: {
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 100, // Make room for fixed bottom
  },
  sectionHeader: {
    fontSize: 11,
    fontWeight: 'bold',
    color: '#8899AA',
    letterSpacing: 1,
    marginBottom: 8,
  },
  horizontalScrollContent: {
    paddingRight: 16,
  },
  incidentCard: {
    width: 200,
    backgroundColor: '#1E3248',
    borderRadius: 8,
    borderTopWidth: 3,
    padding: 12,
    marginRight: 12,
  },
  incidentCardCritical: {
    borderTopColor: '#E84040',
  },
  incidentCardHigh: {
    borderTopColor: '#F5A623',
  },
  incidentCardMedium: {
    borderTopColor: '#8899AA',
  },
  incidentCardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 6,
  },
  incidentTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#FFFFFF',
    flex: 1,
  },
  timeText: {
    fontSize: 11,
    color: '#8899AA',
  },
  chipRow: {
    flexDirection: 'row',
    marginBottom: 8,
  },
  statusChip: {
    borderRadius: 6,
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
  chipCritical: {
    backgroundColor: '#E84040',
  },
  chipWarning: {
    backgroundColor: '#F5A623',
  },
  chipMuted: {
    backgroundColor: '#8899AA',
  },
  chipTealBG: {
    backgroundColor: '#00C2B2',
  },
  statusChipText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  incidentDetails: {
    fontSize: 12,
    color: '#8899AA',
  },
  spacer: {
    height: 20,
  },
  spacerBetweenCards: {
    height: 12,
  },
  aiAssessmentCard: {
    backgroundColor: '#1E3248',
    borderLeftWidth: 3,
    borderLeftColor: '#00C2B2',
    padding: 16,
    borderRadius: 8,
  },
  tealLabel: {
    fontSize: 11,
    color: '#00C2B2',
    fontWeight: 'bold',
    letterSpacing: 1,
    marginBottom: 8,
  },
  assessmentSeverity: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#E84040',
    textAlign: 'center',
    marginVertical: 4,
  },
  assessmentConfidence: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    textAlign: 'center',
    marginBottom: 12,
  },
  divider: {
    height: 1,
    backgroundColor: '#2A4060',
    marginVertical: 12,
  },
  signalRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  signalText: {
    fontSize: 13,
    color: '#FFFFFF',
    marginLeft: 8,
  },
  mutedLabelCaps: {
    fontSize: 11,
    fontWeight: 'bold',
    color: '#8899AA',
    letterSpacing: 1,
    marginBottom: 4,
  },
  reasoningItalic: {
    fontSize: 12,
    fontStyle: 'italic',
    color: '#8899AA',
    lineHeight: 18,
  },
  actionCard: {
    backgroundColor: '#1E3248',
    padding: 16,
    borderRadius: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderLeftWidth: 3,
  },
  actionTealBorder: {
    borderLeftColor: '#00C2B2',
  },
  actionAmberBorder: {
    borderLeftColor: '#F5A623',
  },
  actionBlueBorder: {
    borderLeftColor: '#8899AA', // Default blue border is styled using muted gray/blue in specs
  },
  actionCardContent: {
    flex: 1,
  },
  actionTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  actionDesc: {
    fontSize: 12,
    color: '#8899AA',
    marginTop: 2,
  },
  simulateButton: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 56,
    backgroundColor: '#00C2B2',
    justifyContent: 'center',
    alignItems: 'center',
  },
  simulateButtonText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
});
